# @Searchable
This annotation signifies that a field is searchable and be considered when you query the Rest API.
By default, all @Searchable fields can be queried for by specifying a 'q' query parameter in a GET request.

```java
@Searchable
private String firstName;
```

__*Only works on String fields__