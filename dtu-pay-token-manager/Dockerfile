FROM maven:3.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY messaging-utilities /app/messaging-utilities
RUN mvn clean install -f /app/messaging-utilities/pom.xml
COPY dtu-pay-token-manager/pom.xml .
COPY dtu-pay-token-manager/src ./src
RUN mvn clean package

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=builder /app/target/dtu-pay-token-manager-1.0.jar .
CMD java -Xmx32m $JAVA_OPTS -jar dtu-pay-token-manager-1.0.jar