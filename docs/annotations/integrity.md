# Integrity Annotations
This chapter is dedicated to adding integrity rules to your models.

# @Required
This annotation signifies that a field is required in all circumstances. If no value is provided for this field in an update or create, an error should be thrown and displayed in a Restful response. This gets handled by the `ObjectEvaluator`

```java
@Required
private String lastName;
```
# @Unique
This annotation signifies that a field is unique. It uses `IntegrityEvaluator` and the `Crud` method `#searchInField(Field, String)` to test the uniqueness of a field.

```java
@Unique
private String email;
```
# @Uneditable
This annotation signifies that a field can only be set once. If you try to update the field, an error should be thrown. This error gets thrown by the `ObjectUpdateEvaluator` and gets shown in the Restful Response.

```java
@Uneditable
private String username;
```
# @Evaluators
An evaluator, or `ObjectEvaluator`, is a way of checking if a RestModel item is valid. Whenever you create/update a RestModel item, it gets run through a list of ObjectEvaluators associated with that particular RestModel. The evaluator evaluates an item by not throwing an exception whenever an item gets through it with the `#evaluate(Object, Class)`. By default, the list contains one evaluator, Elepy's default ObjectEvaluator. This default ObjectEvaluator handles things like the `@Number`, `@Text`, `@Required` and `@DateTime`annotations. It makes sure an object doesn't violate the constraints set by those annotations.

__But__, there are ways you can create and assign your own Evaluators. Let's say you want to evaluate a Person RestModel that has an email property, and you want to validate that e-mail address.

You can use the `@Evaluators` to point to a PersonEvaluator(and maybe more, like a generic NameEvaluator) like so:

_Example_
```java
@RestModel(name = "Persons", slug = "/persons")
@Evaluators(PersonEvaluator.class)
public class Person {
    private String id, firstName, lastName, email;

    //Getters and Setters. 
}
```
Where PersonEvaluator would be:

```java
public class PersonEvaluator implements ObjectEvaluator<Person> {

    @Inject
    private EmailValidator emailValidator;

    public void evaluate(Person person, Class<Person> cls) throws Exception {
        boolean isValid = emailValidator.isValid(person.getEmail());

        if (!isValid) {
            throw new ElepyException("Email is not valid", 400);
        }
    }
}
```

# @Searchable
This annotation signifies that a field is searchable and be considered when you query the Rest API.
By default, all @Searchable fields can be queried for by specifying a 'q' query parameter in a GET request.

```java
@Searchable
private String firstName;
```

__*Only works on String fields__