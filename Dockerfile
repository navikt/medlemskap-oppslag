FROM navikt/java:11

COPY build/libs/*.jar ./
COPY build/libs/app*.jar app.jar