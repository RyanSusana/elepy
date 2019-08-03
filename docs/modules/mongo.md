This module is dedicated to the use of MongoDB as a database for Elepy.

## Download

Latest Version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.elepy/elepy/badge.svg)](https://search.maven.org/search?q=com.elepy)
```xml
<dependency>
    <artifactId>elepy-mongo</artifactId>
    <groupId>com.elepy</groupId>
    <version>${elepy.version}</version>
</dependency>
```


## Usage


```java

//configure Mongo as you wish.
MongoClient mongoClient = new MongoClient();

new Elepy()
    .withConfiguration(MongoConfiguration.of(mongoClient, "database-name"))
```


### Enable GridFS file upload with Mongo
It's as easy as just defining a bucket

```java

//configure Mongo as you wish.
MongoClient mongoClient = new MongoClient();

new Elepy()
    .withConfiguration(MongoConfiguration.of(mongoClient, "database-name", "bucket-name"))
```

## Alternative
[Feel free to try out Elepy with SQL as well.](https://github.com/RyanSusana/elepy/tree/master/hibernate)
