# Important Annotations
These are the annotations that you __MUST__ know about before using Elepy.
# @Model
This annotation is the only one you __need__ to mark your POJO with to make it a valid Elepy model. If you don't add this annotation, Elepy will not recognize your model. The __required__ properties are `path ` and `name`. The __optional__ properties are `description`, `shouldDisplayOnCMS`, `defaultSortField` and `defaultSortDirection`.

_Example_
```java
@Model(path = "/products",
            name = "Products",
            description = "The things that I'm selling",
            defaultSortField = "id",
            defaultSortDirection = SortOption.ASCENDING)
public class Products{
    private long id;
}
```
