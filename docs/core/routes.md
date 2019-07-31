# Routes
# Using Routes
Routing in Elepy is __heavily__ inspired by [Sparkjava](http://sparkjava.com/documentation#routes) and [Javalin](https://javalin.io/documentation#endpoint-handlers).

## HttpContext
You will mostly work with this class when dealing with [Handlers](core/handlers), you will usually get an HttpContext passed to you.

It exposes many useful methods, but here are the most important:
``` java
context.request()       //Gets the HttpRequest
context.response()      //Gets the HttpResponse

// More coming soon...
```
## HttpRequest
``` java
request.attributes();             // the attributes list
request.attribute("foo");         // value of foo attribute
request.attribute("A", "V");      // sets value of attribute A to V
request.body();                   // request body sent by the client
request.bodyAsBytes();            // request body as bytes
request.cookies();                // request cookies sent by the client
request.headers();                // the HTTP header list
request.headers("BAR");           // value of BAR header
request.host();                   // the host, e.g. "example.com"
request.ip();                     // client IP address
request.params("foo");            // value of foo path parameter
request.params();                 // map with all parameters
request.pathInfo();               // the path info
request.port();                   // the server port
request.queryParams();            // the query param list
request.queryParams("FOO");       // value of FOO query param
request.queryParamsValues("FOO")  // all values of FOO query param
request.requestMethod();          // The HTTP method (GET, ..etc)
request.scheme();                 // "http"
request.session();                // session management
request.splat();                  // splat (*) parameters
request.uri();                    // the uri, e.g. "http://example.com/foo"
request.url();                    // the url. e.g. "http://example.com/foo"

// More coming soon...
```

## HttpResponse

``` java
response.result();             // get response content
response.result("Hello");      // sets content to Hello
response.header("FOO", "bar"); // sets header FOO with value bar
response.redirect("/example"); // browser redirect to /example
response.status();             // get the response status
response.status(401);          // set status code to 401
response.type();               // get the content type
response.type("text/xml");     // set content type to text/xml

// More Coming soon...
```


# Adding your own Routes
### HttpService
An HttpService is what Elepy uses to create Http Routes.

HttpServices have three main handler types: before-handlers, endpoint-handlers, and after-handlers. They require three parts.

- A verb, ex: before, get, post, put, delete, after
- A path, ex: `/`, `/hello-world`
- A handler implementation `httpContext -> { ... }`

The Handler interface has a void return type. You use ctx.result() to set the response which will be returned to the user.

You will only ever have to worry about HttpServices when you want to add extra routes to Elepy via [Extensions](core/extensions).

### @ExtraRoutes & @Route
_Coming soon..._