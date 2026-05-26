# ===================
# === build stage ===
# ===================

# Use maven + Java 17 JDK base image
FROM maven:3.9.7-eclipse-temurin-17-alpine AS build

WORKDIR /backend

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package

# ========================
# === production stage ===
# ========================

FROM eclipse-temurin:17-jre-alpine AS production

COPY --from=build /backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
