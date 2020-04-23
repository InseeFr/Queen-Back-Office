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
 
  
#### Define the JAVA_OPTS
Set the value of JAVA_OPTS  
```shell
JAVA_OPTS=-Dspring.profiles.active=prod -Dspring.config.location=classpath:/,${catalina.base}/webapps/insee.properties
``` 
This line will define the profile to use and the location of external properties.  

#### Properties file
In the classpath define before, create insee.properties and complete the following properties:  
```shell  
database.url=jdbc:postgresql://your/path/to/DB  
database.username=username  
database.password=password  
  
defaultSchema=schema  

application.mode=Basic #define the authentication mode (Basic, Keycloak or NoAuth) 
application.crosOrigin=* # define the CROS origin url

keycloak.realm=QueenAPI
keycloak.resource=QueenAPI
keycloak.auth-server-url=http://localhost:8180/auth
keycloak.ssl-required=external
keycloak.public-client=true
keycloak.principal-attribute:preferred_username	 
```  

### 4. Tomcat start
From a terminal navigate to tomcat/bin folder and execute  
```shell
catalina.bat run (on Windows)
```  
```shell
catalina.sh run (on Unix-based systems)
```  

### 5. Application Access
To access to swagger-ui, use this url : [http://localhost:8080/queen-0.0.1-SNAPSHOT/swagger-ui.html](http://localhost:8080/queen-0.0.1-SNAPSHOT/swagger-ui.html)  
To access to keycloak, use this url : [http://localhost:8180](http://localhost:8180)  

## Before you commit
Before committing code please ensure,  
1 - README.md is updated  
2 - A successful build is run and all tests are sucessful  
3 - All newly implemented APIs are documented  
4 - All newly added properties are documented  

## End-Points
- Operation
	- `GET /operations` : get the operation list

- Questionnaire
	- `GET /operation/{id}/questionnaire` : get the model json 

- ReportingUnit
	- `GET /operation/{id}/reporting-units` : get the list of reporting unit of operation id

- Data
	- `GET /reporting-unit/{id}/data` : get the data of reporting unit id
	- `PUT /reporting-unit/{id}/data` : update the data of reporting unit id

- Comment
	- `GET /reporting-unit/{id}/comment` : get the comment of reporting unit id 
	- `PUT /reporting-unit/{id}/comment` : update the comment of reporting unit id

- RequiredNomenclature
	- `GET /operation/{id}/required-nomenclatures` : list of nomenclature codes use for operation

- Nomenclatures
	- `GET /nomenclature/{id}` : get the nomenclature json

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

## License
Please check [LICENSE](https://github.com/InseeFr/Queen-Back-Office/blob/master/LICENSE) file
