# Integrity Annotations
This chapter is dedicated to adding integrity rules to your models.

# @Required
This annotation signifies that a field is required in all circumstances. If no value is provided for this field in an update or create, an error should be thrown and displayed in a Restful response. This gets handled by the `ObjectEvaluator`

```java
@Required
private String lastName;
```


# @Evaluators
An evaluator, or `ObjectEvaluator`, is a way of checking if a RestModel item is valid. Whenever you create/update a RestModel item, it gets run through a list of ObjectEvaluators associated with that particular RestModel. The evaluator evaluates an item by not throwing an exception whenever an item gets through it with the `#evaluate(Object, Class)`. By default, the list contains one evaluator, Elepy's default ObjectEvaluator. This default ObjectEvaluator handles things like the `@Number`, `@Text`, `@Required` and `@DateTime`annotations. It makes sure an object doesn't violate the constraints set by those annotations.

__But__, there are ways you can create and assign your own Evaluators. Let's say you want to evaluate a Person RestModel that has an email property, and you want to validate that e-mail address.

You can use the `@Evaluators` to point to a PersonEvaluator(and maybe more, like a generic NameEvaluator) like so:

_Example_
```java
@Model(name = "Persons", path = "/persons")
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
            throw ElepyException.translated(400, "Email is not valid");
        }
    }
}
```

