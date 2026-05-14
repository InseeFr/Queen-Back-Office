FROM eclipse-temurin:25.0.3_9-jre-jammy

WORKDIR /opt/app

COPY ./target/*.jar /opt/app/app.jar

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
ENV JAVA_USER_ID=10001
ENV JAVA_USER=java

RUN groupadd -g "$JAVA_USER_ID" "$JAVA_USER" && \
    useradd -r -u "$JAVA_USER_ID" -g "$JAVA_USER" "$JAVA_USER" && \
    mkdir -p /opt/app/temp-files && \
    chown -R "$JAVA_USER":"$JAVA_USER" /opt/app

USER $JAVA_USER

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]