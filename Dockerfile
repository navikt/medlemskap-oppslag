FROM navikt/java:11

COPY build/libs/*.jar ./
USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl