#!/usr/bin/env bash
#
# scripts/test-in-docker.sh — compile and run regression tests
#
# - Compiles app sources to WEB-INF/classes using container's servlet-api.jar
# - Compiles tests from WEB-INF/test into WEB-INF/test-classes
# - Runs Java main-based tests (no JUnit dependency) in the container
# - Propagates host TZ to container (if set) so time assertions match
#
# ust web application, Copyright (c) 2025 Heiko Lübbe, MIT License, https://github.com/muhme/ust

set -euo pipefail

CONTAINER_NAME=ust

echo "UST/TEST: Starting"

if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  echo "UST/TEST: Container ${CONTAINER_NAME} is not running. Start it first (scripts/build.sh)." >&2
  exit 1
fi

RUN_TESTS_CMD='\
  set -eu; \
  APP=/usr/local/tomcat/webapps/ust; \
  CLASSES="$APP/WEB-INF/classes"; \
  TEST_CLASSES="$APP/WEB-INF/test-classes"; \
  SERVLET_JAR=/usr/local/tomcat/lib/servlet-api.jar; \
  TEST_PATTERN="${TEST_PATTERN:-*Test.java}"; \
  echo "UST/TEST: Compiling app sources..."; \
  javac -d "$CLASSES" -cp "$SERVLET_JAR" "$APP/WEB-INF/src/de/hlu/ust"/*.java; \
  echo "UST/TEST: Compiling tests..."; \
  rm -rf "$TEST_CLASSES"; mkdir -p "$TEST_CLASSES"; \
  javac -d "$TEST_CLASSES" -cp "$SERVLET_JAR:$CLASSES" "$APP/WEB-INF/test/de/hlu/ust"/*.java; \
  echo "UST/TEST: Running tests (pattern: $TEST_PATTERN)..."; \
  ran=0; \
  for f in "$APP/WEB-INF/test/de/hlu/ust"/$TEST_PATTERN; do \
    [ -e "$f" ] || continue; \
    cls=$(basename "$f" .java); \
    echo "UST/TEST: -> Running de.hlu.ust.$cls"; \
    java -cp "$TEST_CLASSES:$CLASSES:$SERVLET_JAR" de.hlu.ust.$cls; \
    ran=$((ran+1)); \
  done; \
  if [ "$ran" -eq 0 ]; then \
    echo "UST/TEST: No tests matched pattern ($TEST_PATTERN)."; \
    exit 1; \
  fi; \
'

if [ -n "${TZ:-}" ]; then
  docker compose exec -e TZ="$TZ" "$CONTAINER_NAME" /bin/sh -c "$RUN_TESTS_CMD"
else
  docker compose exec "$CONTAINER_NAME" /bin/sh -c "$RUN_TESTS_CMD"
fi

echo "UST/TEST: Finished"
