FROM eclipse-temurin:11

MAINTAINER RhysXia

ADD server/build/libs/server-1.0-SNAPSHOT.jar /explore-server.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar","/explore-server.jar"]
