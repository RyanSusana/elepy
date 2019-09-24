# Configuration

The Elepy object is of fluent nature. Most of the methods in the class return the Elepy object.

``` java
var elepy = new Elepy()                     // Begin creating

.onPort(int)                                // Change the port
.withHttpService(HttpService)               // Change the HttpService
.withFileService(FileService)               // Change the FileService
.withDefaultCrudFactory(CrudFactory)        // Change the default CrudFactory
.withBaseEvaluator(ObjectEvaluator)         // Change the base ObjectEvaluator
.addModel(Class)                            // Add a model
.addModels(Class...)                        // Add multiple models in one call
.addModelPackage(String)                    // Add a package of models (e.g com.mypackage.models)
.addExtension(ElepyExtension)               // Add an extension to Elepy
.registerDependency(Class<T>, T)            // Register a dependency that can later be dependency injected
.onStop(EventHandler);                      // Adds an event before Elepy stops

elepy.http();                               // Gets the HttpService to add routes
elep.models();                              // Gets all models registered to Elepy
elepy.objectMapper();                       // Gets the Jackson ObjectMapper
elepy.start();                              // Starts Elepy
elepy.stop();                               // Stops Elepy

// More coming soon...
```