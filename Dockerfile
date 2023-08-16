FROM ghcr.io/graalvm/graalvm-community:17 AS builder
WORKDIR /workspace
COPY . /workspace
ENV SKIP_TESTS=e2e
RUN ./gradlew build

FROM ghcr.io/graalvm/graalvm-community:17
WORKDIR /workspace
COPY . /workspace
COPY --from=builder /workspace/build/libs/allocation.jar /app/allocation.jar
CMD java -jar /app/allocation.jar
