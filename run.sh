#!/bin/sh

./docker.sh

echo ">>> Starting all needed containers..."
docker-compose up -d

echo ">>> Done. Ready to serve"
