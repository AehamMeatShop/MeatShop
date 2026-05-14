FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# 1. بس pom.xml و mvnw - هاد ما بيتغير كثير
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw


RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B


COPY src ./src


RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]