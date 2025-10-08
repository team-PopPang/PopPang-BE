FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar

ENV TZ=Asia/Seoul

EXPOSE 8500

ENTRYPOINT ["java","-jar","app.jar"]