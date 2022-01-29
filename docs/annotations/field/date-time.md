# @DateTime
This annotation signifies that the value of a field is a Date value. It comes with three optional properties, includeTime, minimumDate and maximumDate.

The evaluation of Dates works on a best guess approach if no format is specified.


_Example_
```java
@DateTime(minimumDate = "20190101", maximumDate = "20220101", includeTime = true, format ="yyyyMMdd")
private Date effectiveDate;
```

__*Can only be used on fields that extend Date__