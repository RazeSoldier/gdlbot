# syntax=docker/dockerfile:1
FROM gradle:8.13.0-jdk23 AS builder

USER gradle
WORKDIR /home/gradle/gdlbot
COPY build.gradle .
COPY config.json .
COPY src ./src

RUN gradle installDist --no-daemon

# -----------

FROM eclipse-temurin:23

WORKDIR /app/gdlbot
COPY --from=builder /home/gradle/gdlbot/build/install/gdlbot /app/gdlbot
COPY config.json .

ENTRYPOINT ["bin/gdlbot"]