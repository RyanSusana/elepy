# Getting Started {docsify-ignore-all}
Elepy is awesome, but more importantly, easy! This guide shows how easy Elepy can be to get started with.

# Step Zero: Basic Terminology
Elepy knows the concept of RestModels. These are regular POJO(or Data Classes, for Kotlin users) annotated with the [@RestModel](/docs/annotations#restmodel) annotation. This is the domain objects of your CMS.

And that leads us to the next term, CMS. CMS means (Headless) Content Management System in the context of Elepy. For more details of what a Headless CMS is, [click here](https://en.wikipedia.org/wiki/Headless_content_management_system).

# Step One: Install Elepy with Maven
The latest versions of Elepy can be downloaded at: https://elepy.com/docs/download

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

# Step Four: Explore!

Once you run Elepy you get access to five basic routes:
```
GET       /products       //Find all products
POST      /products       //Create a new product
PUT       /products/:id   //Update a whole product
PATCH     /products/:id   //Update just parts of a product
DELETE    /products/:id   //Delete a product
```

You will also be able to access the admin panel at http://localhost:7777/admin.

## Example Repo
You can find a GitHub repository with a similar backend at: https://github.com/RyanSusana/elepy-basic-example