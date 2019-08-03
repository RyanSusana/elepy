# Getting Started {docsify-ignore-all}
Elepy is awesome, but more importantly, easy! This guide shows how easy Elepy can be to get started with.

___or if you prefer videos...___
<iframe width="560" height="315" src="https://www.youtube.com/embed/4kyScR_lSTM" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

Feel free to subscribe!
# Step Zero: Basic Terminology
Elepy knows the concept of RestModels. These are regular POJO(or Data Classes, for Kotlin users) annotated with the [@RestModel](/docs/annotations#restmodel) annotation. This is the domain objects of your CMS.
And that leads us to the next term, CMS. CMS means [(Headless) Content Management System](https://en.wikipedia.org/wiki/Headless_content_management_system) in the context of Elepy.
[More about the core concepts of Elepy can be found here](/coming-soon.md).    
# Step One: Install Elepy with Maven
For this guide  we will use Elepy with an in-memory version of Mongo.
The latest version of Elepy is:   
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.elepy/elepy/badge.svg)](https://search.maven.org/search?q=com.elepy)
``` xml
<!-- The core basic Elepy dependency combines the core module and the cms module -->
<dependency>
    <groupId>com.elepy</groupId>
    <artifactId>elepy-basic</artifactId>
    <version>LATEST_VERSION</version>
</dependency>

<!-- The MongoDB module for Elepy -->
<dependency>
    <groupId>com.elepy</groupId>
    <artifactId>elepy-mongo</artifactId>
    <version>LATEST_VERSION</version>
</dependency>

<!-- The in-memory MongoDB -->
<dependency>
    <groupId>de.bwaldvogel</groupId>
    <artifactId>mongo-java-server</artifactId>
    <version>1.16.0</version>
</dependency>
```

# Step Two: Create and annotate your POJO's
Create your Rest Model. The only mandatory annotation is `@RestModel`. This annotation is where you describe the name and `/slug` of your model. 
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
# Step Three: Configure Elepy

In the `main()` of your application is where you usually configure and start Elepy. The most important part of the configuration is the Database setup. For MongDB you need to supply Elepy with a reference to your `DB` object. This can be done with the `connectDB(DB)` method or with the `registerDependency(Class, singleton)` method. After connecting your Database, you should start adding your RestModels with the `addModel(Class)`, `addModels(Class...)` and `addModelPackage(String)` methods. Once you do that, you might be interested in adding Extensions. The ElepyAdminPanel Extension is how you can add the Content Management System.

As you also may have noticed, the `Elepy` configuration object is of fluent nature.
``` java
public static void main(String[] args) {
    new Elepy()
        .addConfiguration(AdminPanel.newAdminPanel())
        .addConfiguration(MongoConfiguration.inMemory())
        .onPort(7777)
        .addModel(Product.class)
        .start();

}
```

# Step Four: Explore!

You will be able to access the CMS at http://localhost:7777/admin.

You also get access to an abundance of routes for your products, here are a few:
```
GET       /products       //Find all products
POST      /products       //Create a new product
PUT       /products/:id   //Update a whole product
PATCH     /products/:id   //Update just parts of a product
DELETE    /products/:id   //Delete a product
```

## Example Repo
You can find a GitHub repository with a similar backend at: https://github.com/RyanSusana/elepy-basic-example