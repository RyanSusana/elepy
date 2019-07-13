This module is dedicated to the use of SQL(Through JPA/Hibernate) as a database for Elepy.

## Download

```xml
<dependency>
    <artifactId>elepy-hibernate</artifactId>
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
