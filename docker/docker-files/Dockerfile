#####################
# COMPILATION STAGE #
#####################
FROM maven:3.8.5-openjdk-17 AS build
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
FROM gisaia/arlas-openjdk:17-distroless

# application placed into /opt/app
WORKDIR /opt/app
COPY --from=build /opt/build/arlas-permissions-server.jar /opt/app/
COPY --from=build /opt/build/conf/configuration.yaml /opt/app/
EXPOSE 9997

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl http://localhost:9997/admin/healthcheck | grep -v "\"healthy\":false" || exit 1

ENV JDK_JAVA_OPTIONS="-Xmx512m -XX:+ExitOnOutOfMemoryError"
CMD ["arlas-permissions-server.jar", "server", "/opt/app/configuration.yaml"]
