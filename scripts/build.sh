#!/usr/bin/env bash
#
# scripts/build.sh — compile app sources inside the Docker container
#
# - Ensures the 'ust' container is running (starts it if needed)
# - Compiles Java sources in /usr/local/tomcat/webapps/ust/WEB-INF/src
# - Uses Tomcat's servlet-api.jar from the container
# - Leaves Tomcat to hot-reload classes automatically
# - Host-independent: no local JDK or servlet JAR required
# - Respects container environment (e.g., TZ) during runtime, not during compile
#
# ust web application, Copyright (c) 2025 Heiko Lübbe, MIT License, https://github.com/muhme/ust

set -euo pipefail

CONTAINER_NAME=ust

echo "UST/BUILD: Starting"

echo "UST/BUILD: Running scripts/clean.sh first."
scripts/clean.sh

echo "UST/BUILD: Rebuild the Docker container ${CONTAINER_NAME}."
docker compose build --no-cache

echo "UST/BUILD: Starting the Docker container ${CONTAINER_NAME}."
docker compose up -d

echo "UST/BUILD: Finished"
