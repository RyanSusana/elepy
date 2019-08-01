This Elepy module is dedicated to the use of Vert.X in Elepy.

It was made to give developers another option with the embedded server. 
More importantly it was made to display and ensure Elepy's hexagonal architecture.


__Note__: Elepy runs blocking I/O code. It is not recommended to use Vert.X right now. 


## Usage
```java
new Elepy().withHttpService(new VertxService()).start();
```
