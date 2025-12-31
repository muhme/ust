#!/usr/bin/env bash
#
# scripts/clean.sh — remove local build artifacts and reset Docker environment
#
# - Deletes /tmp/ust*, clearing host-side build/test outputs
# - Runs `docker compose down` to stop and remove the container/network
# - Leaves your mapped data directory intact (no volume purging)
# - Prepares for a fresh `docker compose up -d --build`
#
# ust web application, Copyright (c) 2025 Heiko Lübbe, MIT License, https://github.com/muhme/ust

set -euo pipefail

# Clean build artifacts and Docker environment for a fresh start.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="${SCRIPT_DIR%/scripts}"
COMPOSE_FILE="$ROOT_DIR/docker-compose.yml"

echo "UST/CLEAN: Starting"

echo "UST/CLEAN: Cleaning local build artifacts ..."
rm -rf /tmp/ust /tmp/ust.war /tmp/ust-tests

echo "UST/CLEAN: Stopping and removing Docker container..."
docker compose -f "$COMPOSE_FILE" down

echo "UST/CLEAN: Finished"
