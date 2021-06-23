# Description
Documents manager that allows Upload and deleting of files. It basically a playground to check up Elasticsearch capabilities as underlying storage Unit.

# Setup
- JDK 11
- Elasticsearch v7.12.1 (https://www.elastic.co/downloads/past-releases#elasticsearch)
- Postman (Optional as Swagger is Enabled)

# Running local
 - Please make sure that your Elastic Instance is Up and running.
 - Under `src/main/resources/application.properties` you can define your preferable index name under `custom.document.index.name`. 
 - On start up it will check whether this index is already existing and if not it will be created and explicit mappings `src\main\resources\explicit_mappings.json` will be applied. 

````
mvn verify
````

````
mvn spring-boot:run
````
Check up swagger UI on: http://localhost:8080/swagger-ui.html

Happy Coding!!!
