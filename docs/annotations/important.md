# Important Annotations
These are the annotations that you __MUST__ know about before using Elepy.
# @RestModel
This annotation is the only one you __need__ to mark your POJO with to make it a valid Elepy model. If you don't add this annotation, Elepy will not recognize your model. The __required__ properties are `slug` and `name`. The __optional__ properties are `description`, `shouldDisplayOnCMS`, `defaultSortField` and `defaultSortDirection`.

_Example_
```java
@RestModel( slug = "/products",
            name = "Products",
            description = "The things that I'm selling",
            shouldDisplayOnCMS = true,
            defaultSortField ='id',
            defaultSortDirection = SortOption.ASCENDING)
public class Products{
    private long id;
}
```
# @Identifier
This annotation signifies that a field is the identifying field of a RestModel. A RestModel can only have one `@Identifier`. Furthermore, the supported Identifier types are String, Long or Integer. 
If no @Identifier is used, Elepy will look for a field with the name 'id', or annotated with `@JsonProperty("id")`

`generated` is a flag that you can set to define if you want Elepy to generate ID's for you or not.

_Example_
```java
@Identifier(generated = false)
private String productId;
```
### @javax.persistence.Id
If you're using Hibernate, you can use this annotation as a replacement.