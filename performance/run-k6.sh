#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
IMAGE="${K6_IMAGE:-grafana/k6:latest}"

if [[ -f "${SCRIPT_DIR}/.env" ]]; then
  set -a
  # shellcheck disable=SC1091
  source "${SCRIPT_DIR}/.env"
  set +a
fi

if [[ -z "${ADMIN_EMAIL:-}" ]]; then
  read -r -p "Admin email: " ADMIN_EMAIL
fi
if [[ -z "${ADMIN_PASSWORD:-}" ]]; then
  read -r -s -p "Admin password: " ADMIN_PASSWORD
  echo
fi

if [[ -z "${ADMIN_EMAIL:-}" || -z "${ADMIN_PASSWORD:-}" ]]; then
  echo "ADMIN_EMAIL and ADMIN_PASSWORD are required (export them, put them in performance/.env, or type them when prompted)."
  exit 1
fi

export ADMIN_EMAIL ADMIN_PASSWORD
export BASE_URL="${BASE_URL:-https://172.16.65.137:444}"
export USER_PASSWORD="${USER_PASSWORD:-n8vR2kLm9pQw4sXz!}"
export USER_COUNT="${USER_COUNT:-50}"
export MOVEMENTS="${MOVEMENTS:-10000}"
export SEED_VUS="${SEED_VUS:-20}"
export VUS="${VUS:-50}"

CMD="${1:-all}"

run_k6() {
  local script="$1"
  echo "Running ${script} against ${BASE_URL} as ${ADMIN_EMAIL}"
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
