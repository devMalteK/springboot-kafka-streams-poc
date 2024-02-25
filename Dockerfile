FROM openjdk:17-jdk-alpine
COPY target/springboot-kafka-streams-poc.jar springboot-kafka-streams-poc.jar
ENTRYPOINT ["java","-jar","/springboot-kafka-streams-poc.jar"]