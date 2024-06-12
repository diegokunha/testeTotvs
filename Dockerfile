FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/backend-api.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
