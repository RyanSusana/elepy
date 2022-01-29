# Validation
Elepy supports the validation of records through the Java Bean Validation standard. Bean validation constraints are placed on fields within your model class.

```java
@Model(...)
class Adult {
    @NotBlank(message = "Every person must have a name")
    private String name;
    @Min(18)
    private int age;
}
```

<!---
TODO add how it looks in CMS
-->

By default, validation happens when records are created and deleted. If you want to validate a record, you can call it via Elepy's `HttpContext` like, so:
```
var record = new Adult();
context.validate(record);
```
