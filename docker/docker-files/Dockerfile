#####################
# COMPILATION STAGE #
#####################
FROM maven:3.8.2-openjdk-17 as build
WORKDIR /opt/build

# selectively add the POM file
ADD pom.xml /opt/build/
# get all the downloads out of the way
RUN mvn verify clean --fail-never

# build all project
COPY . /opt/build/
RUN mvn install \
    && mv /opt/build/arlas-permissions-server/target/arlas-permissions-server-*.jar /opt/build/arlas-permissions-server.jar

###################
# PACKAGING STAGE #
###################
FROM gisaia/arlas-openjdk:17
WORKDIR /opt/app

# install script dependencies
RUN apt-get update && apt-get install -y bash netcat-openbsd curl

# application placed into /opt/app
WORKDIR /opt/app
COPY --from=build /opt/build/arlas-permissions-server.jar /opt/app/
COPY --from=build /opt/build/conf/configuration.yaml /opt/app/
COPY --from=build /opt/build/docker/docker-resources/start.sh /opt/app/
EXPOSE 9997

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl http://localhost:9997/admin/healthcheck | grep -v "\"healthy\":false" || exit 1

ENTRYPOINT ["bash", "-c"]
CMD ["/opt/app/start.sh"]
