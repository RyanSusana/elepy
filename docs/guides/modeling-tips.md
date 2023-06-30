# Modeling Tips

Making the right decisions when modeling your domain is crucial for the success of your application. This page is
dedicated to helping you make the right decisions when modeling your domain.

## Think of the end-user

Ultimately, the end-user is the one who will be using your application. It is therefor important to think of the
end-user when modeling your domain. Use the semantics of the end-user when naming your models and properties. Avoid
making things to generic and abstract. Consider using User-Experience focused annotations to make it easier for the
end-user to use your application. Some good ones are: `@Label` and `@Description`.

## Prefer 'has a' relationships over 'relates to' relationships

When modeling your domain, you will often find yourself in a situation where you need to decide whether to use a
'relates to' or a 'has a' relationship.

Before deciding, it's important to understand the difference between the two.

### 'has a' Relationships

In a 'has a' relationship, one model is dependent on the other. The model that is dependent on the other model, is
called
the 'owner' model. The model that is being depended on is called the 'owned' model. It is therefor better to represent
both models as a 'single unit' by embedding the 'owned' model in the 'owner' model.

Use a `@Embedded` annotation to represent 'has a' relationships.

### 'relates to' Relationships

In a 'relates to' relationship, two models are independent of each other, but they have some type of logical connection.

The two models have separate lifecycles and transactional boundaries. They are essentially treated as two separate
units.

Use a `@Reference(to = OtherModel.class)` annotation to represent 'relates to' relationships.

Note: When referring to a model, joining the data of models must often be done client-side. Therefor it is better to
prefer 'has a' relationships over 'relates to' relationships.


