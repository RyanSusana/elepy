![Travis Button](https://travis-ci.com/RyanSusana/elepy.svg?branch=master)

# About Elepy

Elepy is a Rest API Generation Framework/Headless Content Management System for Java/Kotlin developed by [Ryan Susana](https://ryansusana.com/). It is extremely customizable! The framework comes bundled with an admin control panel that lets you easily control your content.

It's able to handle extremely complex objects with ease.
Read this article: https://medium.com/@ryansusana/rapidly-create-a-customized-cms-with-this-awesome-java-framework-e04ef2ea7810

## Downloads
The latest versions of elepy can be found at: https://elepy.com/#DOWNLOADS
### Elepy Core
The core module of Elepy, can be installed with maven. This includes the API generation and the core functionality of Elepy. For the CMS you must include the `elepy-admin` dependency.
``` xml
<dependency>
    <groupId>com.elepy</groupId>
    <artifactId>elepy-core</artifactId>
    <version>LATEST VERSION</version>
</dependency>
```

### Elepy Admin
This is the admin module of Elepy. It contains the powerful content management system.
``` xml
<dependency>
    <groupId>com.elepy</groupId>
    <artifactId>elepy-admin</artifactId>
    <version>LATEST VERSION</version>
</dependency>
```

## Quick Start
### Step One: Create and annotate your POJO's
Create your Rest Model. The only mandatory annotation is `@RestModel`. This annotation is where you describe the name and /slug of your model. You should also take a look at [the awesome collection of Elepy annotations](#annotations). 
``` java
@RestModel(name = "Products", slug = "/products")
public class Product {

    @Identifier
    private String productId;
    
    @Text(value = TextType.TEXTAREA, maximumLength = 100)
    private String shortDescription;
    
    @Text(TextType.HTML)//WYSIWYG editor
    private String htmlDescription;

    @PrettyName("Product Name")
    @Required
    @Unique
    private String name;

    @Number(minimum = 0)
    private BigDecimal price;

    @Number(minimum = 0)
    private int stockLeft;

    //Getters and Setters. I like to use Lombok to automate this :D
}
```
### Step Two: Configure Elepy
``` java
public static void main(String[] args) {
    DB database = mongo.getDB("product-database");

    new Elepy()
        .registerDependency(DB.class, database)
        .withIPAddress("localhost")
        .onPort(7777)
        .addModel(Product.class)
        //Add an Elepy extension
        //The AdminPanel/CMS is a great start :D
        .addExtension(new ElepyAdminPanel())
        .start();

}
```
### Step Three: Enjoy!

You can now login to the Elepy CMS by going to http://localhost:7777/admin. The username and password is `admin`. You can change at the 'Users' interface.

Visit https://github.com/RyanSusana/elepy-basic-example for an example
