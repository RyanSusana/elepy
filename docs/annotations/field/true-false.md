# @TrueFalse
This annotation signifies that a field is a boolean. It comes with two optional properties, `trueValue` and `falseValue`. These values describe what true and false mean in the context of your application.

_Example_
```java
@TrueFalse(trueValue = "This product can be deleted", falseValue = "This product can't be deleted")
private boolean deletable;
```
__*Can only be used on Boolean type fields__