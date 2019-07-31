# Data Type Annotations
This chapter is dedicated to explaining the various annotations that help you define data types within your `@RestModel`.
By default, you don't need any of them, but they're __EXTREMELY__ useful to know. You can put these on any field in your `@RestModel` class.
# @Text

This annotation signifies that the value of a field is a String value. It comes with three optional properties, minimumLength, maximumLength and value(TextType). The minimumLength and maximumLength properties get asserted by the `ObjectEvaluator`. The value(or TextType) changes the way the field is presented in the CMS.
```java
@Text(minimumLength = 50, maximumLength = 350, value = TextType.MARKDOWN)
private String productDescription;
```

__*Can only be used on String Fields__

# @Number
This annotation signifies that a field is a number. It comes with three optional properties, minimum, maximum and value(NumberType). The minimum and maximum properties get asserted by the `ObjectEvaluator'

```java
@Number(minimum = 0, maximum = 1000, value = NumberType.DECIMAL)
private BigDecimal price;
```
__*Can only be used on Fields that extend `java.lang.Number`__

# @DateTime
This annotation signifies that the value of a field is a Date value. It comes with three optional properties, includeTime, minimumDate and maximumDate. 


_More about @DateTime coming soon..._

__*Can only be used on Date Fields__
# @TrueFalse
This annotation signifies that a field is a boolean. It comes with two optional properties, `trueValue` and `falseValue`. These values describe what true and false mean in the context of your application.

```java
@TrueFalse(trueValue = "This product can be deleted", falseValue = "This product can't be deleted")
private boolean deletable;
```
__*Can only be used on Boolean type fields__
# @Array
_More about @Array coming soon..._

__*Can only be used on fields that extend List or Set__

# @FileReference
_More about @FileReference coming soon..._

__*Can only be used on String fields.__