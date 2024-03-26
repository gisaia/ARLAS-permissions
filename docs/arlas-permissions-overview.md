# ARLAS Permissions API Overview

The ARLAS Permissions offers 3 APIs:

- an `authorize` API to get [Resources](arlas-api-permissions.md).
- an API for monitoring the server health and performances
- endpoints for testing the write API and the status API with swagger

## Monitoring

The monitoring API provides some information about the health and the performances of the ARLAS Permissions that can be of interest:

| URL | Description |
| --- | --- |
| http://.../admin/metrics?pretty=true  |  Metrics about the performances of the ARLAS Permissions.|
| http://.../admin/ping | Returns pong  |
| http://.../admin/threads | List of running threads |
| http://.../admin/healthcheck?pretty=true  |  Whether the service is healthy or not |


## Swagger

| URL                                              | Description                                                    |
|--------------------------------------------------|----------------------------------------------------------------|
| http://.../arlas_permissions_server/swagger      | The web application for testing the API                        |
| http://.../arlas_permissions_server/openapi.yaml | The swagger definition of the permissions API with YAML format |
| http://.../arlas_permissions_server/openapi.json | The swagger definition of the permissions API with JSON format |

