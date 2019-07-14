This module is dedicated to the use of MongoDB as a database for Elepy.

[Feel free to try out Elepy with SQL as well.](https://github.com/RyanSusana/elepy/tree/master/hibernate)
## Download

```xml
<dependency>
    <artifactId>elepy-mongo</artifactId>
    <groupId>com.elepy</groupId>
    <version>THE ELEPY VERSION</version>
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
