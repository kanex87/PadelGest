# Fase 1: compilar el servidor amb Maven 
FROM maven:3.9-eclipse-temurin-21 AS compilacio
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

# Fase 2: executar-lo només amb Java (imatge més petita)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=compilacio /app/target/servidor-0.0.1-SNAPSHOT.jar servidor.jar
# Hora d'Espanya: la disponibilitat i les reserves fan servir l'hora actual
ENV TZ=Europe/Madrid
EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=Europe/Madrid", "-XX:MaxRAMPercentage=75", "-jar", "servidor.jar"]
