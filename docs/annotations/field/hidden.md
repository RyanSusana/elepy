# @Hidden
This annotation signifies that a field or model should be hidden from the CMS.

_Example_
```java
@Hidden
private String keepOutOfCMS;
```

When placed on a `@Model`, it hides the model from the CMS.

_Example_
```java
@Model(...)
@Hidden
class HiddenModel{
    long id;
}
```