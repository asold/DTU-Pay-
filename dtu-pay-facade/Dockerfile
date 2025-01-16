FROM maven:3.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY messaging-utilities /app/messaging-utilities
RUN mvn clean install -f /app/messaging-utilities/pom.xml
COPY dtu-pay-facade/pom.xml .
COPY dtu-pay-facade/src ./src
RUN mvn clean package

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=builder /app/target/quarkus-app /app/quarkus-app
CMD java -Xmx32m $JAVA_OPTS -jar quarkus-app/quarkus-run.jar
