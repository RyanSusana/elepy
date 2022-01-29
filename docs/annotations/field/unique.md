# @Unique
This annotation signifies that a field is unique. It uses `IntegrityEvaluator` and the `Crud` method `#searchInField(Field, String)` to test the uniqueness of a field.

```java
@Unique
private String email;
```