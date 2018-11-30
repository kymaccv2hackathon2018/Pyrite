# hackathon
Shared repository for hackathon

Initial plan is a simple application running
that can detect when a user might have abandoned their cart and 
send an email if they don't follow-through, possibly suggesting
some discounted items:

1. Has a lambda running
2. Lambda is listening on a `cart.*` event.
3. Lambda forwards event to spring-boot abandonment app.
4. abandon app sends an email.

## Setup

* Install docker and kubectl on your machine.
* Connect to <https://console.sa-hackathon-10.cluster.extend.sap.cx/home/settings/organisation> and download the kubeconfig file.
* Move the downloaded file `kubeconfig` to something more meaningful (note: expires daily!) `mv ~/Download/kubeconfig ./kubeconfig-10-monday.yaml`
* `export KUBECONFIG=kubeconfig-10-monday.yaml`
* `kubectl get nodes`, `kubectl get pods --all-namespaces`, `kubectl get events --sort-by '{.lastTimestamp}'`, etc...

## Container Registry

Container registry `pyrite.azurecr.io` is available with instructions on slack.  

TODO: create image pull secret 

