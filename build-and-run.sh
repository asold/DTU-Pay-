#!/bin/bash
set -e  # Exit immediately if a command exits with a non-zero status.
./dtu-pay-account-manager mvn package
./dtu-pay-facade mvn package
./dtu-pay-payment-service mvn package
./dtu-pay-report-service mvn package
./dtu-pay-token-manager mvn package

docker compose build
docker compose up -d
sleep 2
# Navigate to the client directory
./dtu-pay-client mvn clean install
echo "Building the Java project in the client directory and running the tests..."
echo "Running Maven tests in the client directory..."
echo "Build and tests completed successfully after pushing to Gitlab!"

