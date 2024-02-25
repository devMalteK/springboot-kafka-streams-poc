# springboot-kafka-streams-poc

A simple App that simulates an EMail subscription service for delivered Parcels.
you can subscribe customers to the service and if a parcel is successfully delivered
a notification event is pushed into a new Kafka topic, so it can be consumed by the email-service.

The main goal is to try out Kafka streams and see if this could be a way to build a subscription service without having
to maintain a separate topic for each customer. And if it's possible to Join two Topics while one Topic has a very low
Record-Count (the Subscription) and the other Topic has thousands/millions of records (the Deliver log).

![alt text](https://github.com/devMalteK/springboot-kafka-streams-poc/blob/main/docs/drawio/Process%20Diagramm.drawio.svg)

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
- maven clean install
- docker-compose -p kafka-streams-poc-app -f docker-compose.yml up --force-recreate --build --remove-orphans

## URLs

- KafkaUI: http://localhost:8080/
- Swagger for Testing: http://localhost:5001/swagger-ui-custom.html
- Webflux Reactive Endpoint: http://localhost:5001/getNotifications

## How to use it

- use KafkaUi if you want to monitor the Topic contents
- use Swagger for Simple Testing
    - generate Subscription for Customer via /subscribe
    - generate a "delivered" Parcel via /delivery/event/generate
    - you should now find your notification in the "notification" topic
- use Swagger for Bulk Load Testing
    - start bulk load via /testdatagenerator/start
    - watch what happens in the kafka-ui
- use the webflux endpoint to get all notification via text-event-streaming
    - http://localhost:5001/getNotifications
    - sadly you can't do that via swagger, because swagger doesn't show the sent Events while the Stream is still open.
      Just open the url in a separate browser tab before you start generating data.

## Conclusion

- works like a charm xD
- i'm very surprised how good the State for the supscription is handled. It was actually very difficult to reset the
  State. I was expecting, that all i have to do was deleting the Topics. But now i know there is also a local state
  store. That makes even more resillient for app crashes than i thought.
- performance is like "near realtime"
- memory usage is also negligible (never got above 500mb)
