#!/bin/bash

echo "Running the application"

docker network inspect edge >/dev/null 2>&1 || docker network create edge

docker compose up -d

