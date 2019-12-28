
# Handlers

Handlers are an important concept in Elepy. They are powerful tools that you can use add custom functionality to your CMS.
You use them in combination with `@Create`, `@Delete`, `@Update`, `@Find`, `@Action`, or `@Service`.

Here is what a typical handler looks like:
```java
public class ProductDelete<Product> implements DeleteHandler<Product> {
    @Override
    public void handleDelete(HttpContext context, Crud<Product> crud, ModelContext<Product> modelContext, ObjectMapper objectMapper) {
        String productId = context.recordId();

        crud.delete(productId);
        context.result("Successfully deleted Product");
    }
}
```

In a handler you get access to:
- `HttpContext` - the complete routing context, [more info can be found here.](core/routes.md)
- `Crud<T>` - access to the database behind the model
- `ModelContext<T>` - access to all information Elepy has about a model.
- Jackson's `ObjectMapper` - For your JSON needs

Using this information, you should be able to do everything you need to do relating to a model.

# Helpful Handlers

### Create
DefaultCreate: The default implementation
SimpleCreate: A simple way to create objects

### FindOne
- DefaultFindOne: The default implementation
- MappedFindOne: Use this class to map the result of a RestModel to another type, such as a DTO hiding passwords.

### FindMany:
- DefaultFindMany: The default implementation
- MappedFindMany: Use this class to map the results of a RestModel to another type.

### FindOne & FindMany:
- MappedFind: This is a combination of MappedFindOne and MappedFindMany. Use this class if you want to always map from a RestModel to another type, regardless if you are finding one or many.

### Update:
DefaultUpdate: The default implementation
SimpleUpdate: A simple way to update objects

### Delete:
DefaultDelete: The default implementation
SimpleDelete: A simple way to delete objects