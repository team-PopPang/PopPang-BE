FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY build/libs/*.jar app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]