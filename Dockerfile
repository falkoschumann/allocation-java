FROM ghcr.io/graalvm/graalvm-community AS builder
WORKDIR /workspace
COPY . /workspace
RUN ./gradlew assemble

FROM ghcr.io/graalvm/graalvm-community
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar allocation.jar
EXPOSE 8080
CMD java -jar allocation.jar
