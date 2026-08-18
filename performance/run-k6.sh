#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE="${K6_IMAGE:-grafana/k6:latest}"

if [[ -z "${ADMIN_EMAIL:-}" || -z "${ADMIN_PASSWORD:-}" ]]; then
  echo "Export ADMIN_EMAIL and ADMIN_PASSWORD for the live SUPER_ADMIN account, then re-run."
  echo "Example:"
  echo "  export ADMIN_EMAIL='admin@example.com'"
  echo "  export ADMIN_PASSWORD='...'"
  echo "  export BASE_URL='https://172.16.65.137:444'"
  echo "  $0 seed"
  echo "  $0 stress"
  exit 1
fi

BASE_URL="${BASE_URL:-https://172.16.65.137:444}"
CMD="${1:-all}"

run_k6() {
  local script="$1"
  docker run --rm --network host \
    -e BASE_URL \
    -e ADMIN_EMAIL \
    -e ADMIN_PASSWORD \
    -e USER_PASSWORD \
    -e USER_COUNT \
    -e MOVEMENTS \
    -e SEED_VUS \
    -e VUS \
    -v "${ROOT}/performance:/scripts:ro" \
    "${IMAGE}" run "/scripts/${script}"
}

case "${CMD}" in
  seed)
    run_k6 seed.js
    ;;
  stress)
    run_k6 stress.js
    ;;
  all)
    run_k6 seed.js
    run_k6 stress.js
    ;;
  *)
    echo "Usage: $0 [seed|stress|all]"
    exit 1
    ;;
esac
