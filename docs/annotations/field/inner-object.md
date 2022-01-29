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
Elepy supports one  type of recursion, _as of now_.