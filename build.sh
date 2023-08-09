#!/usr/bin/env bash

task=$1
case $task in
  clean) ./gradlew clean ;;
  *) ./gradlew build ;;
esac
