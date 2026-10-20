FROM eclipse-temurin:8-jre-alpine

RUN apk add --no-cache tzdata
ENV TZ=Europe/Moscow

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY target/FootballBot-1.0-SNAPSHOT.jar application.jar

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-Xss1m","-jar","application.jar"]
