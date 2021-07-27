#!/bin/sh

_start() {
    CURRENT_UID=$(id -u):$(id -g) MUID=$(id -u) docker-compose start

    # Sleep 5 seconds to wait for dockers up
    echo "-------- 5 seconds until starting Quarkus..." && sleep 5
    ./mvnw quarkus:dev
}

_stop() {
    CURRENT_UID=$(id -u):$(id -g) MUID=$(id -u) docker-compose stop

    kill $(ps aux | grep 'quarkus:dev' | grep 'superchat-backend-challenge-ando' | awk '{print $2}') > /dev/null 2>&1
    if [ $? -eq 0 ];then
       echo "Terminated quarkus superchat-backend-challenge-ando"
    fi
}

_reset() {
    _stop > /dev/null 2>&1

    CURRENT_UID=$(id -u):$(id -g) MUID=$(id -u) docker-compose rm --force
    CURRENT_UID=$(id -u):$(id -g) MUID=$(id -u) docker-compose up --build --no-start

    ./mvnw clean compile -DskipTest -T4
}

_usage() {
  echo "$0 is to start the dev environment in daemon processes."
  echo "Usage:" && grep " .)\ #" $0;
  exit 0;
}

# Parse the command
stop=0
reset=0
while getopts ":srh:" opt; do
  case $opt in
    s) # Stop dev
      stop=1
      ;;
    r) # Reset dev environment before starting
      reset=1
      ;;
    h | *) # Show help
      _usage
      exit 0
      ;;
  esac
done

# Stop dev
if [ $stop = 1 ]; then
  _stop
  exit 0
fi
# Start dev
if [ $reset = 1 ]; then
  _reset
fi
_start