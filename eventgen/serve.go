package main

import (
	"bytes"
	"fmt"
	"net/http"
	"os"
	"strings"
	"time"

	jsonpb "github.com/golang/protobuf/jsonpb"
	"github.com/urfave/cli"
	c "github.wdf.sap.corp/team-pyrite/hackathon/proto/commerce"
)

var sendCommand = &cli.Command{
	Name:  "send",
	Usage: "send web content",
	Flags: []cli.Flag{
		&cli.StringFlag{
			Name:   "endpoint",
			Usage:  "Name of service to send events to",
			Value:  "cart-abandonment-detector-stage.sa-hackathon-10.cluster.extend.sap.cx",
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
	dryRun := ctx.Bool("dryRun")

	// printEnvVars()

	// Populate sites
	bikes := createSite("1", "Nice Bikes")

	// Populate products
	frame := createProduct("1", "Carbon Fiber Frame")
	wheels := createProduct("2", "Carbon Fiber Wheels")
	seatPost := createProduct("3", "Carbin Fiber Seatpost")

	// Populate users
	rafal := createCustomer("1", "Rafal", "rafal@example.com")
	// yoni := createCustomer("2", "Yoni", "yoni@example.com")
	// mike := createCustomer("3", "Mike", "mike@example.com")

	// Populate carts
	rafalCart := createCart(bikes, rafal, "1")
	// yoniCart := createCart(bikes, yoni, "2")
	// mikeCart := createCart(bikes, mike, "3")

	// Add products to carts
	rafalFrame := addProductToCart(rafal, rafalCart, frame, now())
	rafalWheels := addProductToCart(rafal, rafalCart, wheels, now())
	rafalSeatPost := addProductToCart(rafal, rafalCart, seatPost, now())

	messages := &c.MessageList{}

	addSiteCreated(messages, bikes, now())

	addProductCreated(messages, frame, now())
	addProductCreated(messages, wheels, now())
	addProductCreated(messages, seatPost, now())

	addCustomerCreated(messages, rafal, now())

	addCartCreated(messages, rafalCart, now())

	addProductAdded(messages, rafalFrame, now())
	addProductAdded(messages, rafalWheels, now())
	addProductAdded(messages, rafalSeatPost, now())

	_, err := post(endpoint, dryRun, messages)
	if err != nil {
		return err
	}

	return nil
}

func now() string {
	return time.Now().Format(time.RFC3339)
}

func post(endpoint string, dryRun bool, messageList *c.MessageList) (*http.Response, error) {
	var buf bytes.Buffer
	marshaler := jsonpb.Marshaler{
		EmitDefaults: true,
		Indent:       "  ",
	}
	marshaler.Marshal(&buf, messageList)

	if dryRun {
		fmt.Printf("messages:\n%s", buf.String())
		return nil, nil
	}

	resp, err := http.Post(endpoint, "application/json", &buf)
	return resp, err
}

func printEnvVars() {
	for _, e := range os.Environ() {
		pair := strings.Split(e, "=")
		fmt.Printf("%s=%s", pair[0], pair[1])
	}
}

func addSiteCreated(m *c.MessageList, site *c.SiteCreated, eventTime string) {
	m.Message = append(m.Message, &c.Message{
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

func addCustomerCreated(m *c.MessageList, customer *c.CustomerCreated, eventTime string) {
	m.Message = append(m.Message, &c.Message{
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

func addProductCreated(m *c.MessageList, product *c.ProductCreated, eventTime string) {
	m.Message = append(m.Message, &c.Message{
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

func addCartCreated(m *c.MessageList, cart *c.CartCreated, eventTime string) {
	m.Message = append(m.Message, &c.Message{
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

func addProductAdded(m *c.MessageList, o *c.ProductAddToCart, eventTime string) {
	m.Message = append(m.Message, &c.Message{
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

// Bare object create methods

func createSite(id, desc string) *c.SiteCreated {
	return &c.SiteCreated{
		SiteId:      id,
		Description: desc,
	}
}

func createProduct(id, desc string) *c.ProductCreated {
	return &c.ProductCreated{
		ProductId:   id,
		Description: desc,
	}
}

func createCustomer(id, name, email string) *c.CustomerCreated {
	return &c.CustomerCreated{
		CustomerId:  id,
		CustomerUid: id,
		Name:        name,
		Email:       email,
	}
}

func createCart(site *c.SiteCreated, customer *c.CustomerCreated, id string) *c.CartCreated {
	return &c.CartCreated{
		CartId:     id,
		BaseSiteId: site.SiteId,
	}
}

func createCartSuccessfulCheckout(
	site *c.SiteCreated,
	customer *c.CustomerCreated,
	cart *c.CartCreated, product *c.ProductCreated) *c.CartSuccessfulCheckout {

	return &c.CartSuccessfulCheckout{
		BaseSiteId: site.SiteId,
		CartId:     cart.CartId,
		UserId:     customer.CustomerId,
	}

}

func addProductToCart(customer *c.CustomerCreated, cart *c.CartCreated, product *c.ProductCreated, eventTime string) *c.ProductAddToCart {
	return &c.ProductAddToCart{
		BaseSiteId: cart.BaseSiteId,
		CartId:     cart.CartId,
		UserId:     customer.CustomerId,
		ProductId:  product.ProductId,
		EventTime:  eventTime,
	}
}
