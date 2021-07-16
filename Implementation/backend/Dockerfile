FROM gradle:7.1.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test 

FROM openjdk:11-jre-slim

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/ /app/
COPY --from=build /home/gradle/src/src/main/resources/application.properties /app/


ENTRYPOINT ["java","-jar","/app/parkview-0.0.1-SNAPSHOT.jar", "--parkview.datasource.jdbc-url=jdbc:postgresql://parkview-postgres:5432/parkview"]

