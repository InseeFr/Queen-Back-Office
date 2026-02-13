FROM eclipse-temurin:21.0.10_7-jre

ENV PATH_TO_JAR=/opt/app/app.jar
WORKDIR /opt/app/
COPY ./target/*.jar $PATH_TO_JAR

ENV JAVA_TOOL_OPTIONS_DEFAULT \
    -XX:MaxRAMPercentage=75



ENV JAVA_USER_ID=10001
ENV JAVA_USER=java

# Setup a non-root user context (security)
RUN groupadd -g "$JAVA_USER_ID" "$JAVA_USER" && \
    useradd -r -u "$JAVA_USER_ID" -g "$JAVA_USER" "$JAVA_USER"

RUN mkdir /opt/app/temp-files
RUN chown -R "$JAVA_USER":"$JAVA_USER" /opt/app

USER $JAVA_USER_ID

ENTRYPOINT [ "/bin/sh", "-c", \
    "export JAVA_TOOL_OPTIONS=\"$JAVA_TOOL_OPTIONS_DEFAULT $JAVA_TOOL_OPTIONS\"; \
    exec java -jar $PATH_TO_JAR" ]