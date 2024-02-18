# springboot-kafka-native-poc

simple App that produces a random KafkaMessage every x seconds , consumes and processes it afterwards

## POC-focus

- java 17
- springboot 3
- spring kafka
- jackson-json-mapper
- native image
- github Co-Pilot

## How to run

- (local docker installed)
- java-version: graal-vm-ce-17
- checkout project
- mvn -Pnative spring-boot:build-image
- docker-compose -f docker-compose.yml up (compose also starts a needed kafka-broker)

## Conclusion

- Model-Classes that are used with JSon-Serialization need a @RegisterReflectionForBinding Configuration (see
  de/kochnetonline/sbkastp/config/NativeImageHints.java)
- app startup time "0.722 seconds" instead of "3.913 seconds"
- memory usage 170MB
- build-time via mvn >15 minutes (mac m1 with 8 Cores enabled)
- mvn build can't be used in our CI/CD pipeline, because Docker-in-Docker is not supported
- reduced ressource usage allows more and faster starting replicas in k8s
- if you need fast startup times and low memory usage, native image is the way to go
- if you need fast build-times, native image is not the way to go

## Conclusion GitHub CoPilot

- really helps to speed up writing code and documentation
- suggested code is not always usable, but helps to get a first idea
- suggested code helps with code-syntax
- while creating an object-mapping-method the suggested code called all the needed setters and
  getters as i intended to do it. that was nice, because thats always annoying to code :-P
