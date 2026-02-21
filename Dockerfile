# Build command:
# docker build -t blk-hacking-ind-juyel-hushen .

# Using Eclipse Temurin JDK 21 on Ubuntu (Linux-based).
# Chosen for:
# - Officially maintained OpenJDK distribution
# - Smaller, secure runtime image
# - Production stability

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/blackrock-autosavings-challenge.jar app.jar

EXPOSE 5477

ENTRYPOINT ["java", "-jar", "app.jar"]