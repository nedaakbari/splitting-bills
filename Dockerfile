FROM eclipse-temurin:21
WORKDIR /app

COPY target/splitting-bills-0.0.1-SNAPSHOT.jar splitting-bills.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "splitting-bills.jar"]
