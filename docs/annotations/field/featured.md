# @Featured
This annotation signifies that this field should be used when referencing a `@Model` or `@InnerObject`.

_Example_

If you want to refer to a Product's title instead of it's ID in the CMS, you would do.
```java
@Model(...)
class Product{
    private long id;
    
    @Featured
    private String title;
}
```