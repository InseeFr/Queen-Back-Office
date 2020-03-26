# Queen-Back-Office
Back-office services for Queen
REST API for communication between QUEEN DB and QUEEN Form (Frontend).

## Requirements
For building and running the application you need:
- [JDK 1.11](https://jdk.java.net/archive/)
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

## Developers
- Benjamin Claudel (benjamin.claudel@keyconsulting.fr)
- Samuel Corcaud (samuel.corcaud@keyconsulting.fr)

## License
Please check [LICENSE](https://github.com/InseeFr/Queen-Back-Office/blob/master/LICENSE) file
