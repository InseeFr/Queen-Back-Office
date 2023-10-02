FROM eclipse-temurin:17-jre-alpine

WORKDIR /opt/app/
COPY ./target/*.jar /opt/app/app.jar

# Setup a non-root user context (security)
RUN addgroup -g 1000 tomcatgroup
RUN adduser -D -s / -u 1000 tomcatuser -G tomcatgroup
RUN chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT ["java", "-jar",  "/opt/app/app.jar"]
