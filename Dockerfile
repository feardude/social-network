FROM eclipse-temurin:23-jre-alpine
LABEL maintainer=feardude
WORKDIR /app
COPY build/libs/social-network-0.1.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
