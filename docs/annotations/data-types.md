# Data Type Annotations
This chapter is dedicated to explaining the various annotations that help you define data types within your `@RestModel`.
By default, you don't need any of them, but they're __EXTREMELY__ useful to know.
# @Text

This annotation signifies that the value of a field is a String value. It comes with three optional properties, min, max and value(TextType). The min and max properties get asserted by the `ObjectEvaluator`. The value(or TextType) changes the way the field is presented in the CMS.

_Example_
```java
@Size(min = 50, max = 350, value = TextType.MARKDOWN)
private String productDescription;
```

__*Can only be used on String Fields__

# @Number
This annotation signifies that a field is a number. It comes with three optional properties, minimum, maximum and value(NumberType). The minimum and maximum properties get asserted by the `ObjectEvaluator'

_Example_
```java
@Number(minimum = 0, maximum = 1000, value = NumberType.DECIMAL)
private BigDecimal price;
```
__*Can only be used on Fields that extend `java.lang.Number`__

# @DateTime
This annotation signifies that the value of a field is a Date value. It comes with three optional properties, includeTime, minimumDate and maximumDate. 

The evaluation of Dates works on a best guess approach if no format is specified.


_Example_
```java
@DateTime(minimumDate = "20190101", maximumDate = "20220101", includeTime = true, format ="yyyyMMdd")
private Date effectiveDate;
```

__*Can only be used on fields that extend Date__

# @TrueFalse
This annotation signifies that a field is a boolean. It comes with two optional properties, `trueValue` and `falseValue`. These values describe what true and false mean in the context of your application.

_Example_
```java
@TrueFalse(trueValue = "This product can be deleted", falseValue = "This product can't be deleted")
private boolean deletable;
```
__*Can only be used on Boolean type fields__


# @FileReference
A FileReference is a link to an uploaded File.
You can add the maximumFileSize(in __Bytes__) and allowedMimeTypes to references.

_Example_
```java
@FileReference(allowedMimeType = 'image/*', maximumFileSize = 10 * 1024 * 1024)
private String bannerImage;
```

__*Can only be used on String fields.__

# @InnerObject
Inner Objects are completely supported by Elepy.
An object can contain any amount of fields(keep in mind user-experience for your CMS users). 
It can also contain any type of field, including other objects.

_Example_
```java
@InnerObject
private CustomObject customObject;
```

Where CustomObject is:

```java
class CustomObject{ 
    // an @Featured nicely gets displayed in the CMS
    @Featured
    private String name;
    
    
    private String html;
}
```

## Recursion
Elepy supports one  type of recursion, _as of now_. Direct recursion

# @Array
Collections are supported in the form of Sets and Lists. The general grouping of collections is called `@Array`.
Arrays support every type of field (including Objects) __EXCEPT__ other arrays.

Arrays also inherit the details of it's generic type. If you want an array of `@TextArea`s, just add that annotation above your `@Array`!

_Example_
```java
@Array(minimumArrayLength = 1, maximumArrayLength = 10, sortable = true)
private List<Translation> translations;
```

Where `Translation` is:

```java
class Translation {
    private String locale;
    
    @Featured
    @PrettyName("Language Name")
    private String languageName;
    
    
    private String content;
}
```

__*Can only be used on fields that extend List or Set__
