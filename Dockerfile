FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY build/libs/url-shortener-service.jar ./url-shortener-service.jar

EXPOSE 8091
ENTRYPOINT ["java", "-jar", "url-shortener-service.jar"]