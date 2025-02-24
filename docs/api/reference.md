<!-- Generator: Widdershins v4.0.1 -->

<h1 id="arlas-permissions-apis">ARLAS Permissions APIs v27.0.1</h1>

> Scroll down for example requests and responses.

Get a list of authorized endpoints/verbs for authenticated users.

Base URLs:

* <a href="/arlas_permissions_server">/arlas_permissions_server</a>

Email: <a href="mailto:contact@gisaia.com">Gisaia</a> Web: <a href="http://www.gisaia.com/">Gisaia</a> 
License: <a href="https://www.apache.org/licenses/LICENSE-2.0.html">Apache 2.0</a>

<h1 id="arlas-permissions-apis-authorize">authorize</h1>

Permissions API

## Returns a list of permissions for the current context/user

<a id="opIdget"></a>

`GET /authorize/resources`

Returns a list of permissions for the current context/user

<h3 id="returns-a-list-of-permissions-for-the-current-context/user-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|filter|query|string|true|A regex to apply to permissions uris in order to filter the returned list.|
|pretty|query|boolean|false|Pretty print|

> Example responses

> 200 Response

```json
[
  {
    "verb": "string",
    "path": "string"
  }
]
```

<h3 id="returns-a-list-of-permissions-for-the-current-context/user-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Successful operation|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Arlas Permissions Error.|[Error](#schemaerror)|

<h3 id="returns-a-list-of-permissions-for-the-current-context/user-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[Resource](#schemaresource)]|false|none|none|
|» verb|string|true|none|none|
|» path|string|true|none|none|

<aside class="success">
This operation does not require authentication
</aside>

# Schemas

<h2 id="tocS_Resource">Resource</h2>
<!-- backwards compatibility -->
<a id="schemaresource"></a>
<a id="schema_Resource"></a>
<a id="tocSresource"></a>
<a id="tocsresource"></a>

```json
{
  "verb": "string",
  "path": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|verb|string|true|none|none|
|path|string|true|none|none|

<h2 id="tocS_Error">Error</h2>
<!-- backwards compatibility -->
<a id="schemaerror"></a>
<a id="schema_Error"></a>
<a id="tocSerror"></a>
<a id="tocserror"></a>

```json
{
  "status": 0,
  "message": "string",
  "error": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|status|integer(int32)|false|none|none|
|message|string|false|none|none|
|error|string|false|none|none|

