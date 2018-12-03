package main

import (
	"os"

	"github.com/sirupsen/logrus"
	"github.com/urfave/cli"
)

// main is the entrypoint for the program
func main() {
	NewApp().Run(os.Args)
}

// NewApp - create a new eventgen app
func NewApp() *cli.App {
	logrus.SetOutput(os.Stdout)
	logrus.SetLevel(logrus.DebugLevel)

	app := cli.NewApp()

	app.Name = "eventgen"
	app.Usage = "generate kyma/commerce events"
	app.Description = "Event generator application"
	app.Flags = []cli.Flag{}
	app.Commands = []cli.Command{
		*sendCommand,
	}

	return app
}
