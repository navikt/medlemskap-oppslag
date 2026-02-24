FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21
COPY target/app.jar app.jar
CMD ["-jar","app.jar"]