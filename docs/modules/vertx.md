This Elepy module is dedicated to the use of Vert.X in Elepy.

It was made to give developers another option with the embedded server. 
More importantly it was made to display and ensure Elepy's hexagonal architecture.


__Note__: Elepy runs blocking I/O code. It is not recommended to use Vert.X right now. 

## Download

Latest Version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.elepy/elepy/badge.svg)](https://search.maven.org/search?q=com.elepy)
```xml
<dependency>
    <artifactId>elepy-vertx</artifactId>
    <groupId>com.elepy</groupId>
    <version>${elepy.version}</version>
</dependency>
```


## Usage
```java
new Elepy().withHttpService(new VertxService()).start();
```
