# @Array
Collections are supported in the form of Sets and Lists. The general grouping of collections is called `@Array`.
Arrays support every type of field (including Objects) __EXCEPT__ other arrays.

Arrays also inherit the details of it's generic type. If you want an array of `@TextArea`s, just add that annotation above your `@Array`!

_Example_
```java
@Array(sortable = true)
private List<Translation> translations;
```

Where `Translation` is:

```java
class Translation {
    private String locale;
    
    @Featured
    @Label("Language Name")
    private String languageName;
    
    
    private String content;
}
```

__*Can only be used on fields that extend List or Set__