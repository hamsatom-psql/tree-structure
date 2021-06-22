# syntax=docker/dockerfile:experimental
FROM openjdk:8-jdk-alpine AS build
WORKDIR /home/gradle/src

COPY . /home/gradle/src
RUN --mount=type=cache,target=/root/.gradle ./gradlew clean build --parallel --configure-on-demand --watch-fs --no-daemon --no-build-cache --refresh-dependencies

FROM openjdk:8-jre-slim

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/tree-structure-backend.jar
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/tree-structure-backend.jar"]
