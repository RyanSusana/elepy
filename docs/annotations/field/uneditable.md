# @Uneditable
This annotation signifies that a field can only be set once. If you try to update the field, an error should be thrown. This error gets thrown by the `ObjectUpdateEvaluator` and gets shown in the Restful Response.

```java
@Uneditable
private String username;
```