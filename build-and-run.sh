#!/bin/bash
set -e  # Exit immediately if a command exits with a non-zero status.

mvn clean install -f messaging-utilities/pom.xml

# Build all Maven projects
for service in messaging-utilities dtu-pay-account-manager dtu-pay-facade dtu-pay-payment-service dtu-pay-report-service dtu-pay-token-manager; do
    echo "Building $service..."
    cd $service
    mvn clean test
#    mvn package
    cd ..
done

# Build and start Docker containers
docker compose build --no-cache
docker compose up -d
sleep 5

# Navigate to the client directory
cd dtu-pay-client
mvn clean install
cd ..

echo "Building the Java project in the client directory and running the tests..."
echo "Running Maven tests in the client directory..."
echo "Build and tests completed successfully after pushing to Gitlab!"
