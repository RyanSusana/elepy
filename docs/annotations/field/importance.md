# @Importance
This annotation defines the order of a field in the CMS. The higher the importance, the higher it's listed in the CMS.

Negative valued @Importance fields(like `@Importance(-5)`) don't get displayed in the table view of your data.

_Example_
```java
@Importance(-1)
private int id;
```