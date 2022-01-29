# @FileReference
A FileReference is a link to an uploaded File.
You can add the maximumFileSize(in __Bytes__) and allowedMimeTypes to references.

_Example_
```java
@FileReference(allowedMimeType = 'image/*', maximumFileSize = 10 * 1024 * 1024)
private String bannerImage;
```

__*Can only be used on String fields.__