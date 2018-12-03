eventutil:
	bazel build //eventgen/lib/eventutil:eventutil_deploy.jar
	cp ./bazel-bin/eventgen/lib/eventutil/eventutil_deploy.jar cart-abandonment-service/lib/eventutil.jar

stage-pyrite-pull-secret:
	kubectl -n stage create secret docker-registry pyrite-pull-secret --docker-server=pyrite.azurecr.io --docker-username=${AZURE_CLIENT_ID} --docker-password=${AZURE_CLIENT_SECRET} --docker-email="paul.johnston02@sap.com"

stage-serviceaccount:
	kubectl -n stage apply -f stage/serviceaccount.yaml

commerce_proto_sources:
	bazel build cart-abandonment-service:commerce_proto_sources \
	&& (cd cart-abandonment-service/src/main/java && jar -xvf ../../../../bazel-genfiles/cart-abandonment-service/commerce_proto_sources/commerce_proto_sources.srcjar)
