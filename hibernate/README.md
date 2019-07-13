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
__*MAKE SURE TO ANNOTATE ELEPY MODELS WITH THE NECESSARY JPA/HIBERNATE ANNOTATIONS__


### From environment
Use this helper method to load the Hibernate configuration from `hibernate.cfg.xml`.
```java
new Elepy()
    .withConfiguration(HibernateConfiguration.fromEnv());
```

### From custom Hibernate configuration

Use this helper method to build dynamic or configurations for Hibernate.
```java
Configuration hibernateConfiguration = new Configuration.configure();

new Elepy()
    .withConfiguration(HibernateConfiguration.of(configuration))
```
