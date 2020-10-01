FROM gradle:6.6.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew bootjar

FROM openjdk:11.0.4-jre-slim

ENV DATASOURCE_USERNAME=${DATASOURCE_USERNAME}
ENV DATASOURCE_URL=jdbc:mysql://${DATASOURCE_URL}
ENV DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD}
ENV EMAIL_USERNAME=${EMAIL_USERNAME}
ENV EMAIL_PASSWORD=${EMAIL_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}
ENV KAFKA_SERVICE_URI="127.0.0.1:9092"

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080
EXPOSE 3306

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]