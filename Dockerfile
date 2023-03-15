FROM ghcr.io/graalvm/graalvm-ce:22 AS builder
WORKDIR /workspace
COPY . /workspace
RUN ./gradlew assemble

FROM ghcr.io/graalvm/graalvm-ce:22
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar allocation.jar
EXPOSE 8080
CMD java -jar allocation.jar
