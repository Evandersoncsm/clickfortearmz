# ---- Estágio de build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Cache de dependências
COPY pom.xml .
RUN mvn -q dependency:go-offline -B

# Compila e empacota
COPY src ./src
RUN mvn -q clean package -DskipTests -B

# ---- Estágio de runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Dados do H2 ficam em volume para persistir entre reinícios do container
VOLUME /app/data
ENV SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/contagemdb;AUTO_SERVER=TRUE

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
