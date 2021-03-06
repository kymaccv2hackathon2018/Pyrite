# CartVisual

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 6.0.8.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

## Deploy

Follow the build instructions, then you can dockerize the project and update the ./deploy/deployment.yaml with the image tag used.

Run `docker build -t <repo:tag> -f ./deploy/Dockerfile .`

Push the docker image to your docker registry and update the `image:` attribute in the ./deploy/deployment.yaml.

Run `kubectl apply -f ./deploy/deployment.yaml -n <namespace>`

### Microfrontend for Kyma

To expose the UI inside of Kyma

Update the ./deploy/microfrontend.yaml to point to the URL that exposes the UI.

Run `kubectl apply -f ./deploy/microfrontend.yaml -n <namespace>`

## Issues
Currently, the networking isn't fully implemented so the service calls to the REST API will fail with a CORS error. Adding a virtual service to the cluster that correctly maps the requests to the correct service, should resolve this.
