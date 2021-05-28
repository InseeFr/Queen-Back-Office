![Build](https://github.com/InseeFr/Queen-Back-Office/actions/workflows/release.yml/badge.svg)

# Queen-Back-Office
Back-office services for Queen  
REST API for communication between Queen DB and Queen UI.

## Requirements
For building and running the application you need:
- [JDK 11](https://jdk.java.net/archive/)
- Maven 3  

## Install and excute unit tests
Use the maven clean and maven install 
```shell
mvn clean install
```  

## Running the application locally
Use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:  
```shell
mvn spring-boot:run
```  

## Application Accesses locally
To access to swagger-ui, use this url : [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)  
To access to h2 console, use this url : [http://localhost:8080/api/h2-console](http://localhost:8080/api/h2-console)  


## Keycloak Configuration 
1. To start the server on port 8180 execute in the bin folder of your keycloak :
```shell
standalone.bat -Djboss.socket.binding.port-offset=100 (on Windows)

standalone.sh -Djboss.socket.binding.port-offset=100 (on Unix-based systems)
```  
2. Go to the console administration and create role investigator and a user with this role.

## Database configuration
Queen-Back-Office manage 2 Database configurations :
1 - PostgreSQL database with JPA connectors
2 - MongoDb database
It is possible to switch between both configurations via the following property: 
```shell
fr.insee.queen.application.persistenceType
```
It can take the 2 following values : MONGODB or JPA
 
## Deploy application on Tomcat server
### 1. Package the application
Use the [Spring Boot Maven plugin]  (https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:  
```shell
mvn clean package
```  
The war will be generate in `/target` repository  

### 2. Install tomcat and deploy war
To deploy the war file in Tomcat, you need to : 
Download Apache Tomcat and unpackage it into a tomcat folder  
Copy your WAR file from target/ to the tomcat/webapps/ folder  

### 3. Tomcat config
Before to startup the tomcat server, some configurations are needed : 
 

#### External Properties file
Create queenbo.properties or colemcobo.properties near war file and complete the following properties:  
```shell  
#Profile configuration
spring.profiles.active=prod

#Logs configuration
fr.insee.queen.logging.path=${catalina.base}/webapps/log4j2.xml
fr.insee.queen.logging.level=DEBUG

#Application configuration
fr.insee.queen.application.mode=noauth
fr.insee.queen.application.crosOrigin=*
fr.insee.queen.application.persistenceType = MONGODB or JPA

#Database configuration
fr.insee.queen.persistence.database.host = queen-db
fr.insee.queen.persistence.database.port = 5432
fr.insee.queen.persistence.database.schema = queen
fr.insee.queen.persistence.database.user = queen
fr.insee.queen.persistence.database.password = queen
fr.insee.queen.persistence.database.driver = org.postgresql.Driver
fr.insee.queen.defaultSchema=public

#Keycloak configuration
keycloak.realm=insee-realm
keycloak.resource=queen-web
keycloak.auth-server-url=http://localhost:8180/auth
keycloak.public-client=true
keycloak.bearer-only=true
keycloak.principal-attribute:preferred_username

#Keycloak roles
fr.insee.queen.interviewer.role=investigator
fr.insee.queen.user.local.role=manager_local
fr.insee.queen.user.national.role=manager_national

#Pilotage Api
fr.insee.queen.pilotage.service.url.scheme=http
fr.insee.queen.pilotage.service.url.host=localhost
fr.insee.queen.pilotage.service.url.port=8081
```

#### External log file
Create log4j2.xml near war file and define your  external config for logs.  

### 4. Tomcat start
From a terminal navigate to tomcat/bin folder and execute  
```shell
catalina.bat run (on Windows)
```  
```shell
catalina.sh run (on Unix-based systems)
```  

### 5. Application Access
To access to swagger-ui, use this url : [http://localhost:8080/queen-1.1.1/swagger-ui.html](http://localhost:8080/queen-1.1.1/swagger-ui.html)  
To access to keycloak, use this url : [http://localhost:8180](http://localhost:8180)  

## Before you commit
Before committing code please ensure,  
1 - README.md is updated  
2 - A successful build is run and all tests are sucessful  
3 - All newly implemented APIs are documented  
4 - All newly added properties are documented  

## End-Points
- Campaign
	- `GET /campaigns` : get the campaign list
	- `POST /campaigns` : post a new campaign
	- `POST /campaign/context` : integrates the context of a campaign
	

- Questionnaire
	- `GET /questionnaire/{id}` : get the questionnaire by his id
	- `GET /campaign/{id}/questionnaires` : get the list of model json 
	- `GET /campaign/{idCampaign}/questionnaire-id` : id of the questionnaire associated to the campaign
	- `POST /questionnaire-models` : post a new questionnaire

- SurveyUnit
	- `GET /survey-unit/{id}` : get survey-unit by id
	- `GET /survey-unit/{id}/deposit-prof` : get the deposit-proof of survey-unit by id
	- `GET /campaign/{id}/survey-units` : get the list of survey-unit of campaign id
	- `PUT /survey-unit/{id}` : update a survey-unit
	- `POST /campaign/{id}/survey-unit` : post a survey-unit for a campaign

- Data
	- `GET /survey-unit/{id}/data` : get the data of reporting unit id
	- `PUT /survey-unit/{id}/data` : update the data of reporting unit id

- Comment
	- `GET /survey-unit/{id}/comment` : get the comment of reporting unit id 
	- `PUT /survey-unit/{id}/comment` : update the comment of reporting unit id

- RequiredNomenclature
	- `GET /campaign/{id}/required-nomenclatures` : list of nomenclature codes use for campaign

- Nomenclatures
	- `GET /campaign/{id}/required-nomenclatures` : get the nomenclature of a campaign
	- `GET /questionnaire/{id}/required-nomenclatures` : get the nomenclature of a questionnaire
	- `GET /nomenclature/{id}` : get the nomenclature json
	- `POST /nomenclature` : post a new nomenclature

- Personalization
	- `GET /survey-unit/{id}/personalization` : get the personalization of a survey-unit
	- `PUT /survey-unit/{id}/personalization` : put the personalization for a survey-unit
	
- Metadata
	- `GET /campaign/{id}/metadata` : get the metadata of a campaign

- Paradata
	- `POST /paradata` : post the metadata of a campaign
	
- StateData
	- `GET /survey-unit/{id}/state-data` : get the state-data of a survey-unit
	- `PUT /survey-unit/{id}/state-data` : put the state-data for a survey-unit
	
- DataSet
	- `POST /create-dataset` : Create dataset

## Libraries used
- spring-boot-jpa
- spring-boot-security
- spring-boot-web
- spring-boot-tomcat
- spring-boot-test
- liquibase
- h2 database
- postgresql
- junit
- springfox-swagger2
- hibernate
- hibernate-types-52 (for jsonb type)
- keycloak 

## Developers
- Benjamin Claudel (benjamin.claudel@keyconsulting.fr)
- Samuel Corcaud (samuel.corcaud@keyconsulting.fr)
- Paul Guillemet (paul.guillemet@keyconsulting.fr)

## License
Please check [LICENSE](https://github.com/InseeFr/Queen-Back-Office/blob/master/LICENSE) file
