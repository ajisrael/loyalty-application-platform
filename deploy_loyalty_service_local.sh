
#!/bin/bash

# Check for --build flag
BUILD=false
for arg in "$@"; do
  if [ "$arg" == "--build" ]; then
    BUILD=true
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
fi

if [ "$BUILD" = true ]; then
    # Check if JAR file exists
    if [ ! -f target/DiscoveryServer-0.0.1-SNAPSHOT.jar ]; then
	echo "Error: JAR file not found for discovery-server"
	exit 1
    fi

    cd ..

    echo "Building api-gateway"
    cd ./api-gateway

    mvn clean package -Dmaven.test.skip=true

    # Check if JAR file exists
    if [ ! -f target/ApiGateway-0.0.1-SNAPSHOT.jar ]; then
	echo "Error: JAR file not found for api-gateway"
	exit 1
    fi

    cd ..

    echo "Building loyalty-service"
    cd ./loyalty-service

    mvn clean package -Dmaven.test.skip=true

    # Check if JAR file exists
    if [ ! -f target/loyalty-service-0.0.1-SNAPSHOT.jar ]; then
	echo "Error: JAR file not found for loyalty-service"
	exit 1
    fi

    cd ..
else
    echo "Skipping build as --build flag was not provided."
fi

echo "Bringing up containers..."
docker compose up --build -d

echo "Docker containers deployed successfully."
