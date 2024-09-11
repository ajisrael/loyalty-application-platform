#!/bin/bash

# Save the current directory
CURRENT_DIR=$(pwd)

# Set the path to the directory containing your docker-compose.yml file
# Replace /path/to/docker-compose.yml with the actual path if needed
COMPOSE_DIR="$CURRENT_DIR/local-infrastructure"

# Change to the directory where the docker-compose.yml file is located
cd $COMPOSE_DIR || { echo "Directory not found: $COMPOSE_DIR"; exit 1; }

echo "Tearing down containers and volumes..."
docker compose down --volumes

echo "Bringing up containers..."
docker compose up -d

echo "Docker containers restarted successfully."

# Return to the original directory
cd $CURRENT_DIR

echo "Returned to original directory: $CURRENT_DIR"

