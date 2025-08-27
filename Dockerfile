# syntax=docker/dockerfile:1
FROM gradle:9.0.0-jdk24 AS builder

WORKDIR /home/gradle/gdlbot
COPY config.json .
COPY app ./app
COPY ph-util ./ph-util
COPY shared ./shared
COPY translation ./translation
COPY util ./util
COPY settings.gradle.kts  ./settings.gradle.kts
COPY gradle/libs.versions.toml  ./gradle/libs.versions.toml

RUN gradle :app:installDist --no-daemon --parallel

# -----------

FROM eclipse-temurin:24

WORKDIR /app/gdlbot
COPY --from=builder /home/gradle/gdlbot/app/build/install/app /app/gdlbot
COPY config.json .

ENTRYPOINT ["bin/gdlbot"]