# @RestModel
This annotation is the only one you __need__ to mark your POJO with to make it a valid Elepy model. If you don't add this annotation, Elepy will not recognize your model. The __required__ properties are `slug` and `name`. The optional properties are `description`, `defaultSortField` and `defaultSortDirection`.

```
name: What do you call the model? e.g 'Products'
slug: What is the URI of the model? e.g '/products'

description: A brief description of the model
defaultSortField: What field do you sort on by default?
defaultSortDirection: How do you sort the model by default? ASCENDING or DESCENDING?
```

# @Identifier
This annotation signifies that a field is the identifying field of a RestModel. A RestModel can only have one `@Identifier`. Furthermore, the supported Identifier types are String, Long or Integer. 
If no @Identifier is used, Elepy will look for a field with the name 'id', or annotated with `@JsonProperty("id")`