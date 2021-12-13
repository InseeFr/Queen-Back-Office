FROM tomcat:8.5-jdk11-slim

RUN rm -rf $CATALINA_HOME/webapps/*
COPY queenbo*.properties colmcolb*.properties log4j2.xml $CATALINA_HOME/webapps/
ADD /target/*.war $CATALINA_HOME/webapps/ROOT.war
ADD /glowroot $CATALINA_HOME

ENV CATALINA_OPTS="$CATALINA_OPTS -javaagent:$CATALINA_HOME/glowroot.jar"

