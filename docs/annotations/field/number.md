# @Number
This annotation signifies that a field is a number. 

_Example_
```java
@Number(value = NumberType.DECIMAL)
private BigDecimal price;
```
__*Can only be used on Fields that extend `java.lang.Number`__
