FROM openjdk:11-jre-slim-buster
RUN groupadd spring
RUN useradd -g spring spring
USER spring:spring
ARG JAR_FILE=./build/libs/parkview-v1.0.jar
COPY ${JAR_FILE} app.jar
COPY ./src/main/resources/application.properties ./
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
