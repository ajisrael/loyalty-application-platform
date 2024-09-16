#!/bin/bash

# Check for --build flag
BUILD=false
for arg in "$@"; do
  if [ "$arg" == "--build" ]; then
    BUILD=true
    break
  fi
done

# Check for --build-all flag
BUILD_ALL=false
for arg in "$@"; do
  if [ "$arg" == "--build-all" ]; then
    BUILD_ALL=true
    break
  fi
done

# Check for --reset flag
RESET=false
for arg in "$@"; do
  if [ "$arg" == "--reset" ]; then
    RESET=true
    break
  fi
done

# Tear down containers and volumes
if [ "$RESET" = true ]; then
    echo "Tearing down containers and volumes..."
    docker compose down --volumes
else
    docker compose down
fi

if [ "$BUILD" = true ] || [ "$BUILD_ALL" = true ]; then
    echo "Building loyalty-service"
    cd ./loyalty-service || exit 1

    mvn clean package -Dmaven.test.skip=true

    # Check if JAR file exists
    if [ ! -f target/loyalty-service-0.0.1-SNAPSHOT.jar ]; then
      echo "Error: JAR file not found for loyalty-service"
      exit 1
    fi

    cd ..
fi

if [ "$BUILD_ALL" = true ]; then
    echo "Building discovery-server"
    cd ./discovery-server || exit 1

    mvn clean package -Dmaven.test.skip=true

    # Check if JAR file exists
    if [ ! -f target/DiscoveryServer-0.0.1-SNAPSHOT.jar ]; then
      echo "Error: JAR file not found for discovery-server"
      exit 1
    fi

    cd ..

    echo "Building api-gateway"
    cd ./api-gateway || exit 1

    mvn clean package -Dmaven.test.skip=true

    # Check if JAR file exists
    if [ ! -f target/ApiGateway-0.0.1-SNAPSHOT.jar ]; then
      echo "Error: JAR file not found for api-gateway"
      exit 1
    fi

    cd ..

else
    echo "Skipping build as --build flag was not provided."
fi

echo "Bringing up containers..."
if [ "$BUILD" = true ] || [ "$BUILD_ALL" = true ]; then
  docker compose up --build -d
else
  docker compose up -d
fi

echo "Docker containers deployed successfully."
