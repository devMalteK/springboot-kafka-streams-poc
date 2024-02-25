# springboot-kafka-native-poc

simple App that simulates an EMail subscription service for delivered Parcels.
you can subscribe customers to the service and if a parcel is successfully delivered
a notification event is pushed into a new Kafka topic, so it can be consumed by the email-service.

The main goal is to try out Kafka streams and see if this could be a way to build a subscription service without having
to maintain a separate topic for each customer.

![alt text](https://github.com/[username]/[reponame]/blob/[branch]/image.jpg?raw=true)

## POC-focus

- java 17
- springboot 3
- spring kafka
- kafka streams
- webflux

## How to run

- (local docker installed)
- java-version: graal-vm-ce-17
- checkout project
- docker-compose -f docker-compose-kafka.yml up (kafka-broker & ui)
- maven clean install
- docker-compose -p kafka-streams-poc-app -f docker-compose.yml up --force-recreate --build --remove-orphans

## URLs

- KafkaUI: http://localhost:8080/
- Swagger for Testing: http://localhost:5001/swagger-ui-custom.html
- Webflux Reactive Endpoint: http://localhost:5001/getNotifications


