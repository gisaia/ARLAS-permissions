# Configuring ARLAS Permissions running environment

## ARLAS Permissions configuration file

ARLAS Permissions is configured with a yaml configuration file.

External module configurations are available online:

| Module     | Link                                                          |
|------------|---------------------------------------------------------------|
| Swagger    | https://github.com/federecio/dropwizard-swagger               |
| Dropwizard | http://www.dropwizard.io/1.0.4/docs/manual/configuration.html |
| Zipkin     | https://github.com/smoketurner/dropwizard-zipkin              |

## Configure ARLAS Permissions as a docker container

#### With environment variables

ARLAS Permissions can run as a docker container. A rich set of properties of the configuration file can be overriden by passing environment variables to the container:

```shell
docker run -ti -d \
   --name arlas-permissions \
   -e "ARLAS_PERMISSIONS_PREFIX=/permissions" \
   gisaia/arlas-permissions:latest
```
All supported environment variables are listed below.

### With file/URL based configuration

Instead of overriding some properties of the configuration file, it is possible to start the ARLAS Permissions container with a given configuration file.

#### File

The ARLAS Permissions container can start with a mounted configuration file thanks to docker volume mapping. For instance, if the current directory of the host contains a `configuration.yaml` file, the container can be started as follow:

```shell
docker run -ti -d \
   --name arlas-permissions \
   -v `pwd`/configuration.yaml:/opt/app/configuration.yaml \
   gisaia/arlas-permissions:latest
```

#### URL

The ARLAS Permissions container can start with a configuration file that is downloaded before starting up. The configuration file must be available through an URL accessible from within the container. The URL is specified with an environment variable:

| Environment variable                | Description                                                                                       |
|-------------------------------------|---------------------------------------------------------------------------------------------------|
| ARLAS_PERMISSIONS_CONFIGURATION_URL | URL of the ARLAS Permissions configuration file to be downloaded by the container before starting |

For instance, if the current directory of the host contains a `configuration.yaml` file, the container can be started as follow:

```shell
docker run -ti -d \
   --name arlas-permissions \
   -e ARLAS_PERMISSIONS_CONFIGURATION_URL="http://somemachine/conf.yaml" \
   gisaia/arlas-permissions:latest
```

## ARLAS Permissions configuration properties

### Server

| Environment variable                  | ARLAS Server configuration variable                    | Default                            | Description                                                                         |
|---------------------------------------|--------------------------------------------------------|------------------------------------|-------------------------------------------------------------------------------------|
| ARLAS_PERMISSIONS_ACCESS_LOG_FILE     | server.requestLog.appenders.currentLogFilename         | arlas-permissions-access.log       |                                                                                     |
| ARLAS_PERMISSIONS_LOG_FILE_ARCHIVE    | server.requestLog.appenders.archivedLogFilenamePattern | arlas-permissions-access-%d.log.gz |                                                                                     |
| ARLAS_PERMISSIONS_APP_PATH            | server.applicationContextPath                          | /                                  | Base URL path                                                                       |
| ARLAS_PERMISSIONS_PREFIX              | server.rootPath                                        | /arlas_permissions_server          | Base sub-path for **general API**, gets appended to `server.applicationContextPath` |
| ARLAS_PERMISSIONS_ADMIN_PATH          | server.adminContextPath                                | /admin                             | Base sub-path for **admin API**, gets appended to `server.applicationContextPath`   |
| ARLAS_PERMISSIONS_PORT                | server.connector.port                                  | 9997                               |                                                                                     |
| ARLAS_PERMISSIONS_MAX_THREADS         | server.maxThreads                                      | 1024                               |                                                                                     |
| ARLAS_PERMISSIONS_MIN_THREADS         | server.minThreads                                      | 8                                  |                                                                                     |
| ARLAS_PERMISSIONS_MAX_QUEUED_REQUESTS | server.maxQueuedRequests                               | 1024                               |                                                                                     |

   
### Logging

| Environment variable                                | ARLAS Server configuration variable                      | Default                  |
|-----------------------------------------------------|----------------------------------------------------------|--------------------------|
| ARLAS_PERMISSIONS_LOGGING_LEVEL                     | logging.level                                            | INFO                     |
| ARLAS_PERMISSIONS_LOGGING_MBEAN_LEVEL               | logging.loggers."javax.management.mbeanserver"           | INFO                     |
| ARLAS_PERMISSIONS_LOGGING_CONSOLE_LEVEL             | logging.appenders[type: console].threshold               | INFO                     |
| ARLAS_PERMISSIONS_LOGGING_FILE                      | logging.appenders[type: file].currentLogFilename         | arlas-Permissions.log    |
| ARLAS_PERMISSIONS_LOGGING_FILE_LEVEL                | logging.appenders[type: file].threshold                  | INFO                     |
| ARLAS_PERMISSIONS_LOGGING_FILE_ARCHIVE              | logging.appenders[type: file].archive                    | true                     |
| ARLAS_PERMISSIONS_LOGGING_FILE_ARCHIVE_FILE_PATTERN | logging.appenders[type: file].archivedLogFilenamePattern | arlas-Permissions-%d.log |
| ARLAS_PERMISSIONS_LOGGING_FILE_ARCHIVE_FILE_COUNT   | logging.appenders[type: file].archivedFileCount          | 5                        |

### Zipkin

| Environment variable                  | ARLAS Server configuration variable | Default               |
|---------------------------------------|-------------------------------------|-----------------------|
| ARLAS_PERMISSIONS_ZIPKIN_ENABLED      | zipkin.enabled                      | false                 |
| ARLAS_PERMISSIONS_ZIPKIN_SERVICE_HOST | zipkin.serviceHost                  | 127.0.0.1             |
| ARLAS_PERMISSIONS_PORT                | zipkin.servicePort                  | 9997                  |
| ARLAS_PERMISSIONS_ZIPKIN_COLLECTOR    | zipkin.collector                    | http                  |
| ARLAS_PERMISSIONS_ZIPKIN_BASEURL      | zipkin.baseUrl                      | http://localhost:9411 |


### CORS, HEADERS for API response

| Environment variable           | ARLAS Server configuration variable | Default                                                                                                                 | Description                                                      |
|--------------------------------|-------------------------------------|-------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------|
| ARLAS_CORS_ENABLED             | arlas_cors.enabled                  | false                                                                                                                   | Whether to configure cors or not                                 |
| ARLAS_CORS_ALLOWED_ORIGINS     | arlas_cors.allowed_origins          | "*"                                                                                                                     | Comma-separated list of allowed origins                          |
| ARLAS_CORS_ALLOWED_HEADERS     | arlas_cors.allowed_headers          | "arlas-user,arlas-groups,arlas-organization,X-Requested-With,Content-Type,Accept,Origin,Authorization,X-Forwarded-User" | Comma-separated list of allowed headers                          |
| ARLAS_CORS_ALLOWED_METHODS     | arlas_cors.allowed_methods          | "OPTIONS,GET,PUT,POST,DELETE,HEAD"                                                                                      | Comma-separated list of allowed methods                          |
| ARLAS_CORS_ALLOWED_CREDENTIALS | arlas_cors.allowed_credentials      | true                                                                                                                    | Whether to allow credentials or not                              |
| ARLAS_CORS_EXPOSED_HEADERS     | arlas_cors.exposed_headers          | "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Location"                                     | Comma-separated list of exposed headers, readable on client side |

### AUTH

| Environment variable    | ARLAS Server configuration variable | Default                                     | Description                                                                          |
|-------------------------|-------------------------------------|---------------------------------------------|--------------------------------------------------------------------------------------|
| ARLAS_AUTH_POLICY_CLASS | arlas_auth_policy_class             | io.arlas.commons.rest.auth.NoPolicyEnforcer | Specify a PolicyEnforcer class to load in order to activate Authentication if needed |

### CACHE

| Environment variable                  | ARLAS Server configuration variable | Default                               | Description                            |
|---------------------------------------|-------------------------------------|---------------------------------------|----------------------------------------|
| ARLAS_PERMISSIONS_CACHE_FACTORY_CLASS | arlas_cache_factory_class           | io.arlas.commons.cache.NoCacheFactory | Factory class to get the cache manager |
| ARLAS_CACHE_TIMEOUT                   | arlas-cache-timeout                 | 60                                    | TTL in seconds of items in the cache   |

### JAVA
  
| Environment variable | Description            |
|----------------------|------------------------|
| ARLAS_XMX            | Java Maximum Heap Size |
| JVM_OPTION           | JVM options            |
