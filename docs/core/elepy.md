# Configuration

# new Elepy()

The Elepy object is of fluent nature. Most of the methods in the class return the Elepy object.



``` java
var elepy = new Elepy()

.withHttpService(HttpService)
.withFileService(FileService)
.withDefaultCrudFactory(CrudFactory)
.withBaseObjectEvaluator(ObjectEvaluator)
.addModel(Class)
.addModels(Class...)
.addModelPackage(String)

elepy.http()
elep
elepy.start();
elepy.stop();
More coming soon...
```