#!/bin/sh

echo ">>> Building Spring Boot JAR executable"
./gradlew clean bootJar

echo ">>> Building Docker image"
docker build . -t feardude/social-network:0.0.1-SNAPSHOT

echo ">>> Docker image is ready to run!"
