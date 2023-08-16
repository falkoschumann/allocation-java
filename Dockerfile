FROM ghcr.io/graalvm/graalvm-community:17 AS builder
WORKDIR /workspace
COPY . /workspace
RUN ./gradlew assemble

FROM ghcr.io/graalvm/graalvm-community:17
WORKDIR /workspace
COPY . /workspace
COPY --from=builder /workspace/build/libs/allocation.jar /app/allocation.jar
CMD java -jar /app/allocation.jar
