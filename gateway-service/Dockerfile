FROM eclipse-temurin:21-jdk-alpine

# Install curl for container healthchecks
RUN apk add --no-cache curl
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]