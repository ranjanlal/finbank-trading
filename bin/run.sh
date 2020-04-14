#!/bin/bash

resetDocker() {
  local containers=$(docker container ls -a | grep 'trading-app\|trading-db')

  if [ -n "$containers" ]; then
    echo "------------------------ [Removing already running containers ...] ------------------------"
    docker-compose down --volumes
  fi
}

setupDocker() {
  echo "------------------------ [Starting application containers now ...] ------------------------"
  docker-compose up --build
}

displayDockerInfo() {
  echo "------------------------ [Listing containers ...] ------------------------"
  docker ps
}

resetDocker
setupDocker
displayDockerInfo
