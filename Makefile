export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1

all: down build up test

build:
	docker-compose build

up:
	docker-compose up -d app

down:
	docker-compose down --remove-orphans

test: up
	./gradlew test

unit-tests:
	./gradlew test --tests "*unit*"

integration-tests: up
	./gradlew test --tests "*integration*"

e2e-tests: up
	./gradlew test --tests "*e2e*"

logs:
	docker-compose logs app | tail -100

clean:
	./gradlew clean

format:
	./gradlew spotlessApply
