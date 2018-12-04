package main

import (
	"bytes"
	"fmt"
	"log"
	"net/http"
	"net/http/httputil"
	"os"
	"strings"
	"sync"
	"time"

	jsonpb "github.com/golang/protobuf/jsonpb"
	"github.com/urfave/cli"
	c "github.wdf.sap.corp/team-pyrite/hackathon/proto/commerce"
)

const Format = "2006-01-02T15:04:05"

const letterBytes = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

var sendCommand = &cli.Command{
	Name:  "send",
	Usage: "send web content",
	Flags: []cli.Flag{
		&cli.StringFlag{
			Name:   "endpoint",
			Usage:  "Name of service to send events to",
			Value:  "https://event-ingress-stage.sa-hackathon-10.cluster.extend.sap.cx",
			EnvVar: "EVENTGEN_ENDPOINT",
		},
		&cli.StringFlag{
			Name:   "service_name",
			Usage:  "Name of service to send events to",
			Value:  "cart-abandonment-detector",
			EnvVar: "CART_ABANDONMENT_DETECTOR_SERVICE_NAME",
		},
		&cli.IntFlag{
			Name:   "service_port",
			Usage:  "Port of service to send events to",
			Value:  8080,
			EnvVar: "CART_ABANDONMENT_DETECTOR_SERVICE_PORT",
		},
		&cli.StringFlag{
			Name:  "home_dir",
			Usage: "Location of the filesystem root",
			Value: "/tmp/eventgen",
		},
		&cli.BoolFlag{
			Name:  "dry_run",
			Usage: "Don't actually send events",
		},
		&cli.IntFlag{
			Name:  "tick_interval",
			Usage: "Seconds interval for http posts",
			Value: 2,
		},
	},
	Action: func(c *cli.Context) error {
		err := send(c)
		if err != nil {
			return cli.NewExitError(fmt.Sprintf("%v", err), 1)
		}
		return nil
	},
}

func send(ctx *cli.Context) error {
	endpoint := ctx.String("endpoint")
	dryRun := ctx.Bool("dry_run")
	tickInterval := ctx.Int("tick_interval")

	// printEnvVars()
	g := NewGenerator(endpoint, dryRun, tickInterval)

	// Populate sites
	bikes := g.createSite("Nice Bikes", "A Nice Bike Store")

	// Populate products
	frame := g.createProduct("Carbon-Fiber-Frame", "Canyon Bike Frame")
	wheels := g.createProduct("Carbon-Fiber-Wheels", "Canyon Fiber Wheelset")
	seatPost := g.createProduct("Carbon-Fiber-Seatpost", "Canyon Seatpost")

	// Populate users
	rafal := g.createCustomer("Rafal", "rafal@example.com")
	yoni := g.createCustomer("Yoni", "yoni@example.com")
	mike := g.createCustomer("Mike", "mike@example.com")

	// Populate carts
	rafalCart := g.createCart(bikes, rafal, "1")
	yoniCart := g.createCart(bikes, yoni, "2")
	mikeCart := g.createCart(bikes, mike, "3")

	g.addSiteCreated(bikes, now())

	g.addProductCreated(frame, now())
	g.addProductCreated(wheels, now())
	g.addProductCreated(seatPost, now())

	g.addCustomerCreated(rafal, now())
	g.addCustomerCreated(yoni, now())
	g.addCustomerCreated(mike, now())

	g.addCartCreated(rafalCart, now())
	g.addCartCreated(yoniCart, now())
	g.addCartCreated(mikeCart, now())

	g.Run()

	return nil
}

func now() string {
	return time.Now().Format(time.RFC3339)
	// return time.Now().Format(Format)
}

func post(endpoint string, dryRun bool, messageList *c.MessageList) (*http.Response, error) {
	var buf bytes.Buffer
	marshaler := jsonpb.Marshaler{
		EmitDefaults: false,
		Indent:       "  ",
	}
	marshaler.Marshal(&buf, messageList)

	if dryRun {
		fmt.Printf("messages:\n%s", buf.String())
		return nil, nil
	}

	resp, err := http.Post(endpoint, "application/json", &buf)
	if err != nil {
		return nil, err
	}

	dump, err := httputil.DumpResponse(resp, true)
	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("%q", dump)

	return resp, err
}

func printEnvVars() {
	for _, e := range os.Environ() {
		pair := strings.Split(e, "=")
		fmt.Printf("%s=%s", pair[0], pair[1])
	}
}

// Bare object create methods

type generator struct {
	endpoint           string
	dryRun             bool
	tickNumber         int
	wg                 sync.WaitGroup
	msgList            *c.MessageList
	msgMutex           sync.Mutex
	doneChan           chan bool
	timeChan, tickChan <-chan time.Time
	sites              map[string]*c.SiteCreated
	customers          map[string]*c.CustomerCreated
	products           map[string]*c.ProductCreated
	carts              map[string]*c.CartCreated
	checkouts          map[string]*c.CartSuccessfulCheckout
}

func NewGenerator(endpoint string, dryRun bool, tickInterval int) *generator {
	return &generator{
		endpoint:  endpoint,
		dryRun:    dryRun,
		msgList:   &c.MessageList{},
		timeChan:  time.NewTimer(time.Second).C,
		tickChan:  time.NewTicker(time.Duration(tickInterval) * time.Second).C,
		sites:     make(map[string]*c.SiteCreated),
		customers: make(map[string]*c.CustomerCreated),
		products:  make(map[string]*c.ProductCreated),
		carts:     make(map[string]*c.CartCreated),
		checkouts: make(map[string]*c.CartSuccessfulCheckout),
	}
}

// Flush posts the
func (g *generator) Flush() error {
	if len(g.msgList.Message) == 0 {
		log.Printf("Deferring flush (no messages)")
		return nil
	}
	log.Printf("Flushing %d messages...", len(g.msgList.Message))

	g.msgMutex.Lock()
	defer g.msgMutex.Unlock()
	_, err := post(g.endpoint, g.dryRun, g.msgList)
	if err != nil {
		return err
	}
	g.msgList.Message = nil
	return err
}

func (g *generator) Run() {
	for {
		select {
		case <-g.timeChan:
			fmt.Println("Timer expired")
		case <-g.tickChan:
			fmt.Println("Ticker ticked")
			g.tickNumber++
			g.addSampleProductsToCart()
			g.deactivateOneCustomer()
			g.addNewCart()
			g.Flush()
		case <-g.doneChan:
			fmt.Println("Done")
			return
		}
	}
}

func (g *generator) Stop() {
	g.doneChan <- true
}

func (g *generator) addNewCart() {
	id := fmt.Sprintf("User%d", g.tickNumber)
	site := g.sites["Nice Bikes"]
	customer := g.createCustomer(id, id+"@example.com")
	cart := g.createCart(site, customer, "cart"+id)
	g.addCartCreated(cart, now())
}

func (g *generator) deactivateOneCustomer() {
	var junk *c.CustomerCreated
	for _, customer := range g.customers {
		if junk == nil && customer.Active {
			junk = customer
			junk.Active = false
			log.Printf("Expired customer %s", junk.CustomerId)
			return
		}
	}
}

func (g *generator) addSampleProductsToCart() {
	for _, customer := range g.customers {
		if !customer.Active {
			log.Printf("Skipping expired customer %s", customer.CustomerId)
			continue
		}
		cart := g.carts[customer.CustomerId]
		if cart == nil {
			panic("Unknown cart for customer " + customer.CustomerId)
		}
		for _, product := range g.products {
			g.addProductToCart(customer, cart, product, now())
		}
	}
}

func (g *generator) createSite(id, desc string) *c.SiteCreated {
	item := &c.SiteCreated{
		SiteId:      id,
		Description: desc,
	}
	g.sites[id] = item
	return item
}

func (g *generator) createProduct(id, desc string) *c.ProductCreated {
	item := &c.ProductCreated{
		ProductId:   id,
		Description: desc,
	}
	g.products[id] = item
	return item
}

func (g *generator) createCustomer(id, email string) *c.CustomerCreated {
	item := &c.CustomerCreated{
		CustomerId:  id,
		CustomerUid: id,
		Name:        id,
		Email:       email,
		Active:      true,
	}
	g.customers[id] = item
	return item
}

func (g *generator) createCart(site *c.SiteCreated, customer *c.CustomerCreated, id string) *c.CartCreated {
	item := &c.CartCreated{
		CartId:     id,
		BaseSiteId: site.SiteId,
	}
	g.carts[customer.CustomerId] = item
	return item
}

func (g *generator) createCartSuccessfulCheckout(
	site *c.SiteCreated,
	customer *c.CustomerCreated,
	cart *c.CartCreated, product *c.ProductCreated) *c.CartSuccessfulCheckout {

	item := &c.CartSuccessfulCheckout{
		BaseSiteId: site.SiteId,
		CartId:     cart.CartId,
		UserId:     customer.CustomerId,
	}

	key := fmt.Sprintf("%s.%s.%s", site.SiteId, cart.CartId, customer.CustomerId)
	g.checkouts[key] = item
	return item
}

func (g *generator) addProductToCart(customer *c.CustomerCreated, cart *c.CartCreated, product *c.ProductCreated, eventTime string) *c.ProductAddToCart {
	item := &c.ProductAddToCart{
		BaseSiteId: cart.BaseSiteId,
		CartId:     cart.CartId,
		UserId:     customer.CustomerId,
		ProductId:  product.ProductId,
		EventTime:  eventTime,
	}
	g.addProductAdded(item, eventTime)
	return item
}

func (g *generator) addSiteCreated(site *c.SiteCreated, eventTime string) {
	g.addMessage(&c.Message{
		SourceId:         "eventgen",
		EventType:        "site-created",
		EventTypeVersion: "v1",
		EventTime:        eventTime,
		Data: &c.Event{
			Type:        c.Event_SITE_CREATED,
			SiteCreated: site,
		},
	})
}

func (g *generator) addCustomerCreated(customer *c.CustomerCreated, eventTime string) {
	g.addMessage(&c.Message{
		SourceId:         "eventgen",
		EventType:        "customer-created",
		EventTypeVersion: "v1",
		EventTime:        eventTime,
		Data: &c.Event{
			Type:            c.Event_CUSTOMER_CREATED,
			CustomerCreated: customer,
		},
	})
}

func (g *generator) addProductCreated(product *c.ProductCreated, eventTime string) {
	g.addMessage(&c.Message{
		SourceId:         "eventgen",
		EventType:        "product-created",
		EventTypeVersion: "v1",
		EventTime:        eventTime,
		Data: &c.Event{
			Type:           c.Event_PRODUCT_CREATED,
			ProductCreated: product,
		},
	})
}

func (g *generator) addCartCreated(cart *c.CartCreated, eventTime string) {
	g.addMessage(&c.Message{
		SourceId:         "eventgen",
		EventType:        "cart-created",
		EventTypeVersion: "v1",
		EventTime:        eventTime,
		Data: &c.Event{
			Type:        c.Event_CART_CREATED,
			CartCreated: cart,
		},
	})
}

func (g *generator) addProductAdded(o *c.ProductAddToCart, eventTime string) {
	g.addMessage(&c.Message{
		SourceId:         "eventgen",
		EventType:        "product-add-to-cart",
		EventTypeVersion: "v1",
		EventTime:        eventTime,
		Data: &c.Event{
			Type:             c.Event_PRODUCT_ADD_TO_CART,
			ProductAddToCart: o,
		},
	})
}

func (g *generator) addMessage(m *c.Message) {
	g.msgMutex.Lock()
	defer g.msgMutex.Unlock()
	g.msgList.Message = append(g.msgList.Message, m)
}

// func RandStringBytes(n int) string {
// 	b := make([]byte, n)
// 	for i := range b {
// 		b[i] = letterBytes[rand.Intn(len(letterBytes))]
// 	}
// 	return string(b)
// }
