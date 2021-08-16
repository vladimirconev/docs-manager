# Description
Documents manager that allows Upload and deleting of files. It is basically a playground to check up Elasticsearch capabilities as underlying storage Unit.

# Setup
- JDK 11
- Maven v3.6.3 +  
- Elasticsearch v7.12.1+ (https://www.elastic.co/downloads/past-releases#elasticsearch)
- Postman (Optional as Swagger is Enabled)
- Docker

# Running local
 - Please make sure that your Elastic Instance is Up and running (**if you don't want to use docker-compose**).
 - Please make sure your **Docker instance is Up and running** otherwise Integration tests (implemented with Test Containers) will fail.   
 - Under `src/main/resources/application.properties` you can define your preferable index name as `custom.document.index.name`. 
 - On start up it will check whether this index is already existing and if not it will be created and explicit mappings `src\main\resources\explicit_mappings.json` will be applied. 
 - Optionally you can spin up Dockerized Elasticsearch instance by executing `docker-compose -f docker-infrastructure.yml up -d`. Important Note: Consider stopping this ES instance while building the project as you'll encounter failure (I'll further investigate and try to resolve it).

**Using Docker Compose:**
````
docker-compose up --build
````

To Build:
````
mvn clean verify
````

Prettier check (optional as on running Build command by default plugin prettier goal is `write`):
````
mvn prettier:check 
````
Details can be found on https://github.com/HubSpot/prettier-maven-plugin in ReadMe section.

To Run:
````
mvn spring-boot:run
````

Check up swagger UI on: `http://localhost:8080/swagger-ui.html` or Import Postman API collection available under `postman-collection` folder.

Happy Coding!!!

# Feature List
- Exception handling
- Swagger UI to document exposed API
- Caching mechanism to improve performance on serving docs content
- API versioning
- Integration tests using Test Containers