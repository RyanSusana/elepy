This Elepy Module is dedicated to the use of FileSystem storage for file uploads to Elepy.


## Download

```xml
<dependency>
    <artifactId>elepy-uploads</artifactId>
    <groupId>com.elepy</groupId>
    <version>THE ELEPY VERSION</version>
</dependency>
```

## Usage
```java
new Elepy()
    .withConfiguration(FileUploadConfiguration.of("/upload/directory/location"))
```