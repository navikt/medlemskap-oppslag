FROM navikt/java:17

COPY build/libs/*.jar ./
USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl