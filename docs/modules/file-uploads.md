This Elepy Module is dedicated to the use of FileSystem storage for file uploads to Elepy.


## Download

Latest Version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.elepy/elepy/badge.svg)](https://search.maven.org/search?q=com.elepy)
```xml
<dependency>
    <artifactId>elepy-uploads</artifactId>
    <groupId>com.elepy</groupId>
    <version>${elepy.version}</version>
</dependency>
```

## Usage
```java
new Elepy()
    .withConfiguration(FileUploadConfiguration.of("/upload/directory/location"))
```