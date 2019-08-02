# CMS-specific Annotations

# @Featured
This annotation signifies that this field should be used when referencing this model or object.

_Example_
If you want to refer to a Product's title instead of it's ID in the CMS, you would do.
```java
@RestModel(...)
class Product{
    private long id;
    
    @Featured
    private String title;
}
```

# @Hidden
This annotation signifies that a field should be hidden from the CMS.

_Example_
```java
@Hidden
private String keepOutOfCMS;
```

# @Importance
This annotation defines the order of a field in the CMS. The higher the importance, the higher it's listed in the CMS.

Negative valued @Importance fields(like `@Importance(-5)`) don't get displayed in the table view of your data.

_Example_
```java
@Importance(-1)
private int id;
```