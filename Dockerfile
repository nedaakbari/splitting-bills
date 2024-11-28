FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/splitting-bills.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
