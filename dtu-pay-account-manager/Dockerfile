FROM maven:3.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY messaging-utilities /app/messaging-utilities
RUN mvn clean install -f /app/messaging-utilities/pom.xml
COPY dtu-pay-account-manager/pom.xml .
COPY dtu-pay-account-manager/src ./src
RUN mvn clean package

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=builder /app/target/dtu-pay-account-manager-1.0.jar .
CMD java -Xmx32m $JAVA_OPTS -jar dtu-pay-account-manager-1.0.jar