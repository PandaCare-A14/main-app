FROM docker.io/library/eclipse-temurin:21-jdk-alpine@sha256:cafcfad1d9d3b6e7dd983fa367f085ca1c846ce792da59bcb420ac4424296d56 AS builder

WORKDIR /src/tk-adpro
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon --parallel

FROM docker.io/library/eclipse-temurin:21-jre-alpine@sha256:4e9ab608d97796571b1d5bbcd1c9f430a89a5f03fe5aa6c093888ceb6756c502 AS runner

ARG USER_NAME=tk-adpro
ARG USER_UID=1000
ARG USER_GID=${USER_UID}

RUN addgroup -g ${USER_GID} ${USER_NAME} \
    && adduser -h /opt/tk-adpro -D -u ${USER_UID} -G ${USER_NAME} ${USER_NAME}

RUN apk add --no-cache curl

USER ${USER_NAME}
WORKDIR /opt/tk-adpro

COPY --from=builder --chown=${USER_UID}:${USER_GID} /src/tk-adpro/build/libs/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=15s --start-period=120s --retries=5 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java \
  -Xmx${MEMORY_LIMIT:-1024m} \
  -Xms512m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  -XX:+OptimizeStringConcat \
  -XX:+UseCompressedOops \
  -Djava.security.egd=file:/dev/./urandom \
  -Dserver.port=${PORT:-8080} \
  -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} \
  -jar app.jar"]