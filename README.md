## Food and Drug Administration demo service

### Description

This is a RESTful service built using Java and Spring Boot that
* Exposes Open FDA (Food and Drug Administration) Drugs@FDA data 
* Allows simple management of user drug record applications

### Prerequisites

* JDK 11
* MongoDB 4.4

### Getting Started

* To run tests, execute `./gradlew clean test`
* To build the project, execute `./gradlew clean build`
* To run the service with default settings, execute `./gradlew bootRun`. This way the service starts on port `8080` with context path `/` and MongoDB uri `mongodb://localhost/fdademo`
* To check that the service is up, open `http://localhost:8080/actuator/health` and make sure that response is `{"status":"UP"}`
* After service is up, following links are available:
  * `http://localhost:8080/v3/api-docs/` - OpenAPI specification
  * `http://localhost:8080/swagger-ui/index.html` - Swagger UI
