FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S pueria && adduser -S pueria -G pueria

COPY --from=build /workspace/target/*.jar app.jar

USER pueria

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
