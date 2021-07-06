FROM tomcat:8.5-jdk11-slim

RUN rm -rf $CATALINA_HOME/webapps/*
COPY queenbo*.properties colmcolb*.properties $CATALINA_HOME/webapps/
ADD /target/*.war $CATALINA_HOME/webapps/ROOT.war
ADD /glowroot/glowroot.jar $CATALINA_HOME/glowroot.jar

ENV CATALINA_OPTS="$CATALINA_OPTS -javaagent:$CATALINA_HOME/glowroot.jar"
