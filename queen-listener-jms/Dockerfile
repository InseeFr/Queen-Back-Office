FROM eclipse-temurin:21-jre-jammy

WORKDIR /opt/app/
COPY ./target/*.jar /opt/app/app.jar

# Setup a non-root user context (security)
RUN groupadd -g 1000 tomcatgroup
RUN useradd -r -u 1000 -g tomcatgroup tomcatuser
RUN mkdir /opt/app/temp-files
RUN chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT ["java", "-jar",  "/opt/app/app.jar"]

