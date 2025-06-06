# Imagem base com Java 17
FROM openjdk:17-jdk-alpine

# Cria diretório da aplicação
WORKDIR /app

# Copia o jar gerado
COPY target/*.jar app.jar

# Define comando de inicialização
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
