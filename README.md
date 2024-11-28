# Splitting-bills
A Java-based application for splitting among group members

<img src="https://github.com/user-attachments/assets/78a4a34d-29d7-4cdf-8f37-ab57f2ad95e8" alt="Description" width="500">

---

## Features
- **User Management**: Register and manage group members.
- **Expense Tracking**: Record and categorize expenses.
- **Cost Splitting**: Automatically split expenses among group members.
- **Reports**: Generate summaries and detailed reports for expenses.
- **API Documentation**: Swagger UI for API testing and documentation.
- **Monitoring**: Spring Boot Actuator for health checks and application monitoring.

---

## Tech Stack
- **Backend**: Java 21 (Spring Boot 3)
- **Database**: PostgreSQL
- **API Documentation**: Swagger/OpenAPI
- **Containerization**: Docker
- **Build Tool**: Maven
- **Monitoring**: Spring Boot Actuator

---

## Prerequisites
- **Java 21** or later
- **Docker** and **Docker Compose**
- **PostgreSQL** installed locally or via Docker
---

## Setup



### 1. Clone the repository
```bash

git clone https://github.com/nedaakbari/splitting-bills.git
cd splitting-bills
 ``` 

### 2. application.properties
Update application.properties with your PostgreSQL credentials:
```bash
spring.datasource.username=
spring.datasource.password=
 ```

### 3. Build the project
```bash
mvn clean install
 ```

### 4. Run using Docker
Ensure docker-compose.yml is configured properly, then:
```bash
docker-compose up --build
 ```

### 5. Access the application
- Swagger UI: http://localhost:8080/swagger-ui.html
- Actuator Endpoints: http://localhost:8080/actuator

---

## API Endpoints

 Monitoring with Actuator:
- Health Check: /actuator/health
- Metrics: /actuator/metrics
- Info: /actuator/info

---

## TODO
- ability to use app without being a memeber
- its better to implement authenticate with oauth2 and sign in with gmail
- refresh token
- invalid other token when ask for a new token
- ratelimitter and limitted user to ask for token too many or call other api 
- can download the excel file of expense of group
- can share group picture
- can have profile photo

