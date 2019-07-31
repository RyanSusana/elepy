# About Elepy {docsify-ignore-all}

Elepy is a Headless Content Management Framework for Java & Kotlin developed by [Ryan Susana](https://ryansusana.com/). It is extremely customizable! The framework comes bundled with an admin control panel that lets you easily control your content.

It's able to handle extremely complex objects with ease.




# Quick Start & Example Repo
Visit https://github.com/RyanSusana/elepy-basic-example for an example of Elepy in action.


## Step One: Create and annotate your POJO's
Create your Rest Model. The only mandatory annotation is `@RestModel`. This annotation is where you describe the name and /slug of your model. You should also take a look at [the awesome collection of Elepy annotations](https://elepy.com/docs/annotations). 
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
## Step Two: Configure Elepy
In this case I've used the [elepy-mongo](https://github.com/RyanSusana/elepy/tree/master/mongo) module to setup the database.
``` java
public static void main(String[] args) {
    MongoClient mongoClient = new MongoClient();

    new Elepy()
        .withConfiguration(MongoConfiguration.of(mongoClient, "product-db"))
        .withIPAddress("localhost")
        .onPort(7777)
        .addModel(Product.class)
        //Add an Elepy extension
        //The AdminPanel/CMS is a great start :D
        .addExtension(new ElepyAdminPanel())
        .start();

}
```
## Step Three: Enjoy!
You can now login to the Elepy CMS by going to http://localhost:7777/admin.
