FROM eclipse-temurin:21
COPY target/lib /usr/src/lib
COPY target/dtu-pay-account-manager-1.0.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx32m -jar dtu-pay-account-manager-1.0.0.jar


#FROM maven:3.9.9-eclipse-temurin-21 AS build
#WORKDIR /app
#
#COPY /pom.xml .
#RUN mvn dependency:go-offline
#
#COPY /src ./src
#RUN mvn clean package -DskipTests
#
#FROM openjdk:21-jdk-slim
#WORKDIR /app
#
#COPY --from=build /app/target/*.jar app.jar
#
#ENTRYPOINT ["java", "-jar", "app.jar"]
