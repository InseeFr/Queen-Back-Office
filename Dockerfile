FROM eclipse-temurin:17-jre-alpine

WORKDIR /opt/app/
COPY ./target/*.jar /opt/app/app.jar
ENTRYPOINT ["java", "-jar",  "/opt/app/app.jar"]
