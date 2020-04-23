FROM tomcat:8.5-jdk11-slim

RUN rm -rf $CATALINA_HOME/webapps/*
ADD queen-bo.properties $CATALINA_HOME/webapps/queen-bo.properties
ADD /target/*.war $CATALINA_HOME/webapps/ROOT.war
