FROM gradle:6.6.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew bootjar

FROM openjdk:11.0.4-jre-slim

ENV DATASOURCE_USERNAME=root
ENV DATASOURCE_URL=jdbc:mysql://hackathon-greenfuxes.c7atmc5cgyxk.eu-west-3.rds.amazonaws.com/greenfuxes?serverTimezone=UTC
ENV DATASOURCE_PASSWORD=password
ENV EMAIL_USERNAME=tesztElemm@gmail.com
ENV EMAIL_PASSWORD=Asd12345
ENV JWT_SECRET=secret
ENV KAFKA_SERVICE_URI="127.0.0.1:9092"

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080
EXPOSE 3306

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]