FROM tomcat:8.5-jdk11-slim

RUN rm -rf $CATALINA_HOME/webapps/*
ADD log4j2.xml $CATALINA_HOME/webapps/log4j2.xml
ADD queen-bo.properties $CATALINA_HOME/webapps/queen-bo.properties
ADD /target/*.war $CATALINA_HOME/webapps/ROOT.war
