
<a name="paths"></a>
## Resources

<a name="authorize_resource"></a>
### Authorize

<a name="get"></a>
#### Returns a list of permissions for the current context/user
```
GET /authorize/resources
```


##### Description
Returns a list of permissions for the current context/user


##### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**filter**  <br>*required*|A regex to apply to permissions uris in order to filter the returned list.|string||
|**Query**|**pretty**  <br>*optional*|Pretty print|boolean|`"false"`|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Successful operation|< [Resource](#resource) > array|
|**500**|Arlas Permissions Error.|[Error](#error)|


##### Consumes

* `application/json;charset=utf-8`


##### Produces

* `application/json;charset=utf-8`



