# Moonwlker
[![Build Status](https://travis-ci.org/bertilmuth/moonwlker.svg?branch=master)](https://travis-ci.org/bertilmuth/moonwlker)

Moonwlker is a facade for the Jackson JSON library.

It enables you to serialize and deserialize JSON objects without annotations in the classes.
This is helpful if you don't have access to the classes, or don't want to annotate them to keep them free of JSON concerns.

You can also (de)serialize objects with an all arguments constructor, without the need for a no-argument constructor or setters.
And you can (de)serialize type hierarchies.

*This project is in an early stage. The API may change.*

# Getting started
Moonwlker is available on Maven Central.

If you are using Maven, include the following in your POM:

``` xml
<dependency>
  <groupId>org.requirementsascode</groupId>
  <artifactId>moonwlker</artifactId>
  <version>0.0.2</version>
</dependency>
```

If you are using Gradle, include the following in your build.gradle:

```
implementation 'org.requirementsascode:moonwlker:0.0.2'
```

At least Java 8 is required, download and install it if necessary.

# All arguments constructor / immutable objects
The standard way in which Jackson supports all arguments constructors is to use the `@JsonCreator` and `@JsonProperties` annotations.
Moonwlker changes that: it enables you to deserialize objects that have a single, all arguments default constructor.

To enable this feature, you need to pass in the `-parameters` compiler argument when compiling your class files.
[This article](https://www.concretepage.com/java/jdk-8/java-8-reflection-access-to-parameter-names-of-method-and-constructor-with-maven-gradle-and-eclipse-using-parameters-compiler-argument#compiler-argument) describes how to do that.

After you've done that, to use this Moonwlker feature, import Moonwlker and create an `ObjectMapper` like in the following example:

``` java
import static org.requirementsascode.moonwlker.Moonwlker.json;
...
ObjectMapper objectMapper = json().mapper();

String jsonString = "{\"price\":412,\"name\":\"Calla\",\"command\":\"Sit\"}";
Dog dog = objectMapper.readValue(jsonString, Dog.class);
```

Here's what the example [Dog class](https://github.com/bertilmuth/moonwlker/blob/master/src/test/java/org/requirementsascode/moonwlker/testobject/animal/Dog.java) looks like:

``` java
public class Dog extends Animal {
  private final String name;
  private final String command;

  public Dog(BigDecimal price, String name, String command) {
    super(price);
    this.name = name;
    this.command = command;
  }
  
  public String name() {
    return name;
  }

  public String command() {
    return command;
  }
}
```

See [this test class](https://github.com/bertilmuth/moonwlker/blob/master/src/test/java/org/requirementsascode/moonwlker/GeneralTest.java) for details on how to deserialize objects with an all arguments constructor.

Normally, Jackson has special behavior for single argument constructors.
Moonwlker changes that: it treats single argument constructors the same to simplify deserialization.

# Integrate into Spring Boot application

To change the default `ObjectMapper` in a Spring Boot application, register Moonwlker's mapper as a bean:

``` java
@SpringBootApplication
public class GreeterApplication {
  public static void main(String[] args) {
    SpringApplication.run(GreeterApplication.class, args);
  }

  @Bean
  ObjectMapper objectMapper() {
    ObjectMapper objectMapper = json().mapper();
    return objectMapper;
  } 
}
```

# (De)serialization of type hierarchies
Build your Jackson object mapper with Moonwlker:

``` java
import static org.requirementsascode.moonwlker.Moonwlker.*;
...
ObjectMapper objectMapper = json("type").to(Person.class).mapper();
```

In the above example, [Person](https://github.com/bertilmuth/moonwlker/blob/master/src/test/java/org/requirementsascode/moonwlker/testobject/person/Person.java) is the super class.
The created `ObjectMapper` (de)serializes objects of direct or indirect subclasses of that super class.
The `type` JSON property needs to specify the relative class name of the object to be created by Moonwlker (i.e. [Employee](https://github.com/bertilmuth/moonwlker/blob/master/src/test/java/org/requirementsascode/moonwlker/testobject/person/Employee.java)):

``` java
String jsonString = "{ \"type\" : \"Employee\", \"firstName\" : \"Jane\", \"lastName\" : \"Doe\" , \"employeeNumber\" : \"EMP-2020\"}";
Employee employee = (Employee) objectMapper.readValue(jsonString, Person.class);
```
Use a simple class name like above if the sub class is in the same package as the super class.
Use a package prefix if the sub class is in a direct or indirect sub package of the super class' package. 
For example, this JSON string could be used if `Employee` was in the `company` subpackage of the package that `Person` is in:

``` java
String jsonString = "{ \"type\" : \"company.Employee\", \"firstName\" : \"Jane\", \"lastName\" : \"Doe\" , \"employeeNumber\" : \"EMP-2020\"}";
```

You can also specify multiple base classes like so:

``` java
ObjectMapper objectMapper = 
    json("kind").to(Animal.class, Person.class).mapper();

String jsonString = "{\"kind\":\"Dog\",\"price\":412,\"name\":\"Calla\",\"command\":\"Sit\"}";
Dog dog = (Dog) objectMapper.readValue(jsonString, Animal.class);
jsonString = "{\"kind\":\"Employee\",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"employeeNumber\":\"EMP-2020\"}";
Employee employee = (Employee) objectMapper.readValue(jsonString, Person.class);
```

See [this test class](https://github.com/bertilmuth/moonwlker/blob/master/src/test/java/org/requirementsascode/moonwlker/SubclassInSamePackageTest.java) for details on how to deserialize classes in the same package as their super class.

You can also define specific packages where subclasses can be found, like so:

``` java
ObjectMapper objectMapper = 
    json("type") 
      .to(Person.class).in("org.requirementsascode.moonwlker.testobject.person")
      .to(Animal.class).in("org.requirementsascode.moonwlker.testobject.animal")
        .mapper();
```

See [this test class](https://github.com/bertilmuth/moonwlker/blob/master/src/test/java/org/requirementsascode/moonwlker/SubclassInSpecifiedPackageTest.java) for details on how to deserialize classes in a specified package.