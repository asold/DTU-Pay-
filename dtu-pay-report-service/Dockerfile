FROM maven:3.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY messaging-utilities /app/messaging-utilities
RUN mvn clean install -f /app/messaging-utilities/pom.xml
COPY dtu-pay-report-service/pom.xml .
COPY dtu-pay-report-service/src ./src
RUN mvn clean package

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=builder /app/target/dtu-pay-report-service-1.0.jar .
CMD java -Xmx32m $JAVA_OPTS -jar dtu-pay-report-service-1.0.jar