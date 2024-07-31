# Custom Functionality

_"Creating a basic API is cool and all, but what happens when I want to do my own thing?"_ - ___the curious Programmer___.

In this guide I will show you how to do it the easy way!

### Step One: Create and Annotate your Model
Below, I have a basic everyday API model, Product.
```java
import com.elepy.annotations.*;

@Model(name = "Products", path = "/products")//The only necessary annotation for Elepy
@Update(handler = ProductUpdate.class, accessLevel = AccessLevel.ADMIN) //Override Elepy's Update :D
public class Product {

    @Identifier // All elepy models must have atleast 1 identifying field. By default it can be a 'String productId;'
    @Label("Product ID") // A nice name to be used in Elepy error messages and such
    @JsonProperty("productId")
    private String productId;


    @Label("Short Description")
    private String shortDescription;

    @Label("Long Description")
    
    @Importance(-10)
    private String htmlDescription;

    @Label("Product Name")
    @Required // All products must have a name
    @Uneditable // You can't edit the product's name after it has been set
    @Unique // Product  names must be unique
    private String name;

    @Label("Product Price")
    @Number
    private BigDecimal price;

    @Label("Amount of stock left")
    @Number
@Min(0)
    private int stockLeft;

    @Label("Amount sold")
    @Number
@Min(0)
    private int amountSold;

  // Getters and Setters
}
```

As you may have already seen, you can override Elepy's functionality with the `@Find`, `@Create`, `@Update` or `@Delete` annotations. When you use one of these annotations you can define two annotation properties, `accessLevel` or `handler`. 

The `accessLevel` property is where you define the level of security on a model route. This can be `DISABLED`, `PROTECTED` or `PUBLIC`. If a route is `DISABLED`, noone can access it. If a route is `PROTECTED`, someone can access it under certain circumstances(that you self-define, such as BasicAuth). If a route is `PUBLIC`, anyone can access it.

The `handler` is where you define which class handles the execution of a route. For an update the default class is `com.elepy.routes.DefaultUpdate`. For our scenario we made a custom handler.

### Step Two: Creating a custom Handler
For updates our handler must implement the interface `UpdateHandler<Product>`. In our example we will use one of Elepy's helper classes: `com.elepy.routes.SimpleUpdate`. This class supplies us with a `beforeUpdate` and a `afterUpdate` method.

In our example below, we make it so that whenever a Product's `amountSold` changes, we automatically update the Product's stock to reflect the change.
``` java
public class ProductUpdate extends SimpleUpdate<Product> {

    private static final Logger logger = LoggerFactory.getLogger(ProductUpdate.class);

    public void beforeUpdate(Product objectToUpdate, Crud<Product> crud, ElepyContext elepy) {
        logger.info("Product is being updated.");

        // execute extra logic code like sending an e-mail to the boss :D
        // throw an exception(preferably ElepyException) to block the update
    }

    public void afterUpdate(Product before, Product after, Crud<Product> crud, ElepyContext elepy) {
        if (before.getAmountSold() != after.getAmountSold()) {
            final int difference = after.getAmountSold() - before.getAmountSold();
            final int currentStock = after.getStockLeft();

            int newStock = currentStock - difference;
            if (newStock < 0) {
                newStock = 0;
            }
            after.setStockLeft(newStock);

            crud.update(after);

            logger.info(String.format("Product has been updated. The stock has been increased/decreased by %d.", difference));
        } else {
            logger.info("Product has been updated.");
        }
    }
}
```

This GitHub repository can be found at: https://github.com/RyanSusana/elepy-basic-example