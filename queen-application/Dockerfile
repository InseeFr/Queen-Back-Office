FROM eclipse-temurin:21.0.7_6-jre-alpine

ENV PATH_TO_JAR=/opt/app/app.jar
WORKDIR /opt/app/
COPY ./target/*.jar $PATH_TO_JAR

ENV JAVA_TOOL_OPTIONS_DEFAULT \
    -XX:MaxRAMPercentage=75

# Setup a non-root user context (security)
RUN addgroup -g 1000 tomcatgroup
RUN adduser -D -s / -u 1000 tomcatuser -G tomcatgroup
RUN mkdir /opt/app/temp-files
RUN chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT [ "/bin/sh", "-c", \
    "export JAVA_TOOL_OPTIONS=\"$JAVA_TOOL_OPTIONS_DEFAULT $JAVA_TOOL_OPTIONS\"; \
    exec java -jar $PATH_TO_JAR" ]