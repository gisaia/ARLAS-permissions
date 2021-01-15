# ARLAS Permissions API

The ARLAS Permissions API lets you retrieve the permissions of a user (uris and associated HTTP methods).

## URL Schema
The table below lists the URL endpoints.

| PATH Template                     | Description                            |
| ----------------------------------| -------------------------------------- |
| /authorize/resources?`filter`     | Returns the list of permissions        |


## Listing permissions

### /authorize/resources

| Method     | Input Data                    | Output Data | Description                                               |
| ---------- | ----------------------------- | ------------| ----------------------------------------------------------|
| **GET**    | `filter` as string (optional) | array  | Returns the list of permissions given the optional filter |


Example of returned JSON:
```json
[
        {
            "verb": "DELETE",
            "path": "swagger.*"
        },
        {
            "verb": "GET",
            "path": "swagger.*"
        },
        {
            "verb": "HEAD",
            "path": "swagger.*"
        },
        {
            "verb": "OPTIONS",
            "path": "swagger.*"
        },
        {
            "verb": "POST",
            "path": "swagger.*"
        },
        {
            "verb": "PUT",
            "path": "swagger.*"
        },
        {
            "verb": "GET",
            "path": "persist.*"
        },
        {
            "verb": "PUT",
            "path": "persist/resource/id/.*"
        },
        {
            "verb": "DELETE",
            "path": "persist/resource/id/.*"
        },
        {
            "verb": "POST",
            "path": "persist/resource/config.json/.*"
        },
        {
            "verb": "DELETE",
            "path": "persist/resource/config.json/.*"
        },
        {
            "verb": "POST",
            "path": "persist/resource/i18n/.*"
        },
        {
            "verb": "DELETE",
            "path": "persist/resource/i18n/.*"
        },
        {
            "verb": "GET",
            "path": "persist/resources/config.json"
        },
        {
            "verb": "GET",
            "path": "persist/resources/i18n"
        },
        {
            "verb": "GET",
            "path": "persist/resource/id/.*"
        },
        {
            "verb": "GET",
            "path": "persist/resource/config.json/.*"
        },
        {
            "verb": "GET",
            "path": "persist/resource/i18n/.*"
        },
        {
            "verb": "GET",
            "path": "persist/groups/config.json"
        },
        {
            "verb": "GET",
            "path": "persist/groups/i18n"
        },
        {
            "verb": "GET",
            "path": "status/\\Qdemo\\E_\\Q\\E.*"
        },
        {
            "verb": "POST",
            "path": "write/\\Qdemo\\E_\\Q\\E.*"
        },
        {
            "verb": "GET",
            "path": "explore/ogc/opensearch/\\Qdemo\\E_\\Q\\E.*"
        },
        {
            "verb": "POST",
            "path": "collections/_import"
        },
        {
            "verb": "GET",
            "path": "collections/_export"
        },
        {
            "verb": "PUT",
            "path": "collections/\\Qdemo\\E_\\Q\\E.*"
        },
        {
            "verb": "DELETE",
            "path": "collections/\\Qdemo\\E_\\Q\\E.*"
        },
        {
            "verb": "GET",
            "path": "collections"
        },
        {
            "verb": "GET",
            "path": "explore/_list"
        },
        {
            "verb": "GET",
            "path": "collections/\\Qdemo\\E_\\Q\\E.*"
        },
        {
            "verb": "GET",
            "path": "explore/\\Qdemo\\E_\\Q\\E.*"
        },
        {
            "verb": "POST",
            "path": "explore/\\Qdemo\\E_\\Q\\E.*"
        }
    ]
```