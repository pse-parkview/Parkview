FROM gradle:7.1.1-jdk11 AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test -x ktlintMainSourceSetCheck -x ktlintTestSourceSetCheck -x detekt

FROM openjdk:11-jre-slim

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/ /app/
COPY --from=build /home/gradle/src/src/main/resources/application.properties /app/

RUN groupadd spring
RUN useradd -g spring spring
USER spring:spring

ENTRYPOINT ["java","-jar", "/app/parkview-v1.0.jar"]
EXPOSE 8080

