#!/bin/bash

# a copier dans prd et recette

echo "Cleaning containers"

docker compose down -v

echo "Pulling images"

docker compose pull

echo "Starting containers"

docker compose up -d