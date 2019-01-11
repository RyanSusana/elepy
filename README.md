# About Elepy

Elepy is a Rest API Generation Framework/Headless Content Management System for Java/Kotlin developed by [Ryan Susana](https://ryansusana.com/). It is extremely customizable! The framework comes bundled with an admin control panel that lets you easily control your content.

It's able to handle extremely complex objects with ease.
## Downloads
### Elepy Core
The core module of Elepy, can be installed with maven. This includes the API generation and the core functionality of Elepy. For the CMS you must include the `elepy-admin` dependency.
```
<dependency>
    <groupId>com.elepy</groupId>
    <artifactId>elepy-core</artifactId>
    <version>1.7.1</version>
</dependency>
```

### Elepy Admin
This is the admin module of Elepy. It contains the powerful content management system.
```
<dependency>
    <groupId>com.elepy</groupId>
    <artifactId>elepy-admin</artifactId>
    <version>1.7.1</version>
</dependency>
```

## Quick Start
### Step One: Create and annotate your POJO's
Create your Rest Model. The only mandatory annotation is `@RestModel`. This annotation is where you describe the name and /slug of your model. You should also take a look at [the awesome collection of Elepy annotations](#annotations). 
```
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
```
public static void main(String[] args) {
    DB database = mongo.getDB("product-database");

    new Elepy()
        .attachSingleton(DB.class, database)
        .ipAddress("localhost")
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

To clone this repo visit: https://github.com/RyanSusana/elepy-basic-example
