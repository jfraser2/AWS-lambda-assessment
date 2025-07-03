#!/usr/bin/env posh
(
  trap 'kill -s KILL 0' INT  # Trap SIGINT and send SIGKILL to the process group
  while kill -s 0 "$$" 2>/dev/null; do sleep 1; done
) &
exec "$@" sam local start-api --docker-network VA-assessment