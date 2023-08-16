export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1

all: down build up test

.PHONY: build up down test unit-tests integration-tests e2e-tests logs clean format

build:
	docker-compose build

up:
	docker-compose up -d app

down:
	docker-compose down --remove-orphans --volumes

test: up
	docker-compose run --rm --no-deps --entrypoint=./gradlew app test

unit-tests:
	docker-compose run --rm --no-deps --entrypoint=./gradlew app test --tests "*unit*"

integration-tests: up
	docker-compose run --rm --no-deps --entrypoint=./gradlew app test --tests "*integration*"

e2e-tests: up
	docker-compose run --rm --no-deps --entrypoint=./gradlew app test --tests "*e2e*"

logs:
	docker-compose logs app | tail -100

clean:
	./gradlew clean

format:
	./gradlew spotlessApply
