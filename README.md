# Description
Documents manager that allows Upload and deleting of files. It basically a playground to check up Elasticsearch capabilities as underlying storage Unit.

# Setup
- JDK 11
- Elasticsearch v7.12.1 (https://www.elastic.co/downloads/past-releases#elasticsearch)
- Postman (Optional as Swagger is Enabled)

# Running local
Please make sure that your Elastic Instance is Up and running.

````
mvn verify
````

````
mvn spring-boot:run
````
Check up swagger UI on: http://localhost:8080/swagger-ui.html

Happy Coding!!!
