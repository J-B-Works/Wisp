# ---------- IMAGE OFICIAL DO MAVEN ----------
# Image pesada do Maven para compilar o código
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# ---------- DEPENDÊNCIAS, CÓDIGO FONTE E DADOS ----------
COPY sources/software_engineering_wisp/pom.xml .
COPY sources/software_engineering_wisp/src ./src
COPY Data ./Data

# OPCIONAL: IGNORANDO TESTES PARA BUILD SER MAIS RÁPIDA
RUN mvn clean package -DskipTests

# ---------- RODA IMAGE MENOR DE JAVA ----------
# Substitui image do Maven para image de Java puro mais leve para rodar o executável
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# ---------- COPIA ARQUIVOS CRIADOS ----------
# Copia e cola os arquivos compilados no Maven para o Java puro
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/Data ./Data

# ---------- INICIALIZAÇÃO ----------
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]