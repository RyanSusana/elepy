# About Elepy

Elepy is a Rest API Generation Tool/Headless Content Management System for Java/Kotlin developed by Ryan Susana. It is extremely customizable! The framework comes bundled with an admin control panel that lets you easily control your content.

Because it's backed by MongoDB, it's able to handle extremely complex objects with ease.

Every part of the generation library is customizable. That means that you can use Elepy to handle really complicated application logic as well.

Everything about this 
## Api Generator
The API generator, Elepy, is the core of the framework. It's super fast, thanks to [spark-java](http://sparkjava.com/) and [jongo](http://jongo.org/).  Most API's (5-6 models) load within 1 second. The api is completely @Annotation based, meaning that you only need to annotate your POJO's and voilà you have a completely configurable REST API for that POJO. The only restriction is that your POJO has a field annotated with `@MongoId` from the Jongo library and your POJO class must also be annotated with `@RestModel` from the Elepy framework.

The generator also comes with handy interfaces and annotations that allow you to add restrictions and specifications to your data models. Some annotations include:

 - Required Fields `@RequiredField`
 - Min value and max value fields for numbers`@Number`
 - Min length and max length attributes text`@Text`
 - Pretty names to be displayed on your front end: `@PrettyName`
 - Non editable fields after the initial create(id's are non-editable by default) `@NonEditable`
 - Searchable fields for the findAll route `@Searchable`
 
 All of these things get handled by Elepy's implementation of it's own ObjectEvaluator and ObjectUpdateEvaluator. You can also add your own ObjectEvaluators to a POJO by adding to the objectEvaluators array in `@RestModel`

All of the routes are configurable with different access types (ADMIN, PUBLIC, DISABLED) and different Route implementations per route type. The route types are: findOne, find, create, update, delete. They each have their own implementations. You are not limited in what you can do with them.

A `/config` route is also generated that displays how your REST API is modeled. It's done recursively, so even the most intense of objects can be modeled accordingly. It also displays all of the data restrictions are also presented in the config JSON.

Elepy is also modular. All modules must extend the ElepyModule class and they can be hooked onto with the `elepyInstance.addModule(...)` method. The image upload module and the Elepy Admin Panel module are examples.




 
