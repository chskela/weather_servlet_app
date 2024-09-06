#FROM gradle:8.6.0-jdk17-alpine AS build
#COPY --chown=gradle:gradle . /home/gradle/src
#WORKDIR /home/gradle/src
#RUN gradle war --no-daemon

FROM tomcat:jre21-temurin-jammy
LABEL authors="chskela"

# We remove any unwanted default webapp
RUN rm -fr "$CATALINA_HOME"/webapps/*

# We copy our WAR application inside tomcat webapps
#COPY --from=build /home/gradle/src/build/libs/weather_servlet_app-1.0.war $CATALINA_HOME/webapps/
COPY ./build/libs/weather_servlet_app-1.0.war $CATALINA_HOME/webapps/

COPY ./server.xml /usr/local/tomcat/conf

EXPOSE 8080
CMD ["catalina.sh", "run"]