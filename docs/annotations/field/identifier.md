# @Identifier
This annotation signifies that a field is the identifying field of a RestModel. A RestModel can only have one `@Identifier`. Furthermore, the supported Identifier types are String, Long or Integer.
If no @Identifier is used, Elepy will look for a field with the name 'id', or annotated with `@JsonProperty("id")`

`generated` is a flag that you can set to define if you want Elepy to generate ID's for you or not, by default it's set to true.

_Example_
```java
@Identifier(generated = false)
private String productId;
```
### @javax.persistence.Id
If you're using Hibernate, you can use this annotation as a replacement.