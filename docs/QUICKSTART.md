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
        .attachSingleton(DB.class, database)
        .withIPAddress("localhost")
        .onPort(7777)
        .addModel(Product.class)
        //Add an Elepy extension
        //The AdminPanel/CMS is a great start :D
        .addExtension(new ElepyAdminPanel())
        .start();

}
```