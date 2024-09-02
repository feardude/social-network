FROM eclipse-temurin:22-jre-alpine
LABEL maintainer=feardude
WORKDIR /app
COPY build/libs/social-network-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
