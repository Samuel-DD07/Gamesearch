#!/bin/bash

# a copier dans gamesearch

echo "baking the image"

docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --push \
  -t ${REGISTRY_URL:-registry.local}/gamesearch-backend:latest \
  -t ${REGISTRY_URL:-registry.local}/gamesearch-backend:${BACKEND_VERSION} \
  .

echo "pushing the image"

docker push ${REGISTRY_URL:-registry.local}/gamesearch-backend:latest
docker push ${REGISTRY_URL:-registry.local}/gamesearch-backend:${BACKEND_VERSION}
