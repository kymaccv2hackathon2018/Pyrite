FROM java:8
VOLUME /tmp
ADD /target/cart-abandonment-service-0.0.1-SNAPSHOT.jar cart_abandonment.jar
RUN bash -c 'touch /cart_abandonment.jar.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/cart_abandonment.jar.jar"]