#!/bin/bash
#
# lint.sh – Run Checkstyle linter on all Java source files inside Docker container
#
# ust web application, Copyright (c) 2025 Heiko Lübbe, MIT License, https://github.com/muhme/ust

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

CHECKSTYLE_VERSION="10.21.1"
CHECKSTYLE_JAR="/tmp/checkstyle-${CHECKSTYLE_VERSION}-all.jar"
CONFIG_FILE="/usr/local/tomcat/webapps/ust/checkstyle.xml"

echo "UST/LINT: Starting Checkstyle linter"

# Check if container is running
if ! docker ps | grep -q "^\w\+\s\+.*\bust\b"; then
    echo "UST/LINT: Starting container..."
    docker compose up -d
    sleep 2
fi

# Download Checkstyle if not present
docker exec ust bash -c "
    if [ ! -f $CHECKSTYLE_JAR ]; then
        echo 'UST/LINT: Downloading Checkstyle 10.21.1...'
        curl -s -L https://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.21.1/checkstyle-10.21.1-all.jar -o $CHECKSTYLE_JAR
    fi
"

# Run Checkstyle on all Java source files
echo "UST/LINT: Running Checkstyle on Java sources..."
docker exec ust bash -c "
    java -jar $CHECKSTYLE_JAR \
        -c $CONFIG_FILE \
        /usr/local/tomcat/webapps/ust/WEB-INF/src/de/hlu/ust/*.java
" || true

echo "UST/LINT: Checkstyle completed"
