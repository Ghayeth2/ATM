FROM openjdk:17-jdk-slim
WORKDIR /atmsemu
COPY target/atm-0.0.1-SNAPSHOT.jar .
COPY src/main/resources/dynamic-configs.properties /atmsemu/dynamic-configs.properties
CMD ["java", "-jar", "atm-0.0.1-SNAPSHOT.jar"]