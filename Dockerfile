FROM eclipse-temurin:17-jre-alpine

MAINTAINER RhysXia

ADD server/build/libs/server-1.0-SNAPSHOT.jar /explore-server.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar","/explore-server.jar"]
