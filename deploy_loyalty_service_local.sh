#!/bin/bash

echo "Tearing down containers and volumes..."
docker compose down --volumes

echo "Building discovery-server"
cd ./discovery-server

mvn clean package -Dmaven.test.skip=true

cd ..

echo "Building api-gateway"
cd ./api-gateway

mvn clean package -Dmaven.test.skip=true

cd ..

echo "Building loyalty-service"
cd ./loyalty-service

mvn clean package -Dmaven.test.skip=true

cd ..


echo "Bringing up containers..."
docker compose up --build

echo "Docker containers restarted successfully."

