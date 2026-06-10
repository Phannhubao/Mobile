FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw dependency:resolve -q
COPY src src
RUN ./mvnw package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=production
ENTRYPOINT ["java", "-jar", "app.jar"]
