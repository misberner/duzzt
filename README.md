Duzzt
================
An Annotation-based Embedded DSL Generator for Java

What is Duzzt?
-----------------
*Duzzt* is a Java Annotation Processor (library) to easily generate *Embedded DSLs* for Java. An Embedded DSL (or EDSL) is a [fluent interface](https://en.wikipedia.org/wiki/Fluent_interface) which follows a certain syntax, i.e., rules that describe legal sequences of method invocation chains. In *Duzzt*, [regular expressions](https://en.wikipedia.org/wiki/Regular_expression) are used to define the syntax of embedded DSLs.


Using Duzzt
-----------------
The easiest way to use *Duzzt* is when using [Apache Maven](http://maven.apache.org). Simply add the following snippet to your `<dependencies>` section:
```
<dependency>
    <groupId>com.github.misberner.duzzt</groupId>
    <artifactId>duzzt-processor</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <!-- This will make sure the dependency is non-transitive! -->
    <scope>provided</scope>
</dependency>
```

If you are not using Maven (and do not use a build system with a compatible dependency management, such as [Apache Ivy](http://ant.apache.org/ivy/), you need the following libraries to be present in your classpath **during compile time**:
* [duzzt-annotations.jar](...), containing the annotations used by Duzzt
* [duzzt-processor.jar](...), containing the actual Duzzt annotation processor
* [stringtemplate.jar](...), the [StringTemplate](http://www.stringtemplate.org/) library
* [antlr-runtime.jar](...), ANTLR runtime required by StringTemplate
* [automaton.jar](...), the [Brics Automaton](http://www.brics.dk/automaton/) library
* [ap-commons.jar](...), an commons library for annotation processing

Note that **none** of these libraries is required during runtime of your program.

Getting Started
-----------------
Check the Wiki for [usage examples](todo) and a small [tutorial](todo). Or take a look at the Javadoc of the main annotation, [`@GenerateEmbeddedDSL`](https://misberner.github.com/duzzt/maven-site/apidocs/com/github/misberner/duzzt/annotations/GenerateEmbeddedDSL.html).


Why Duzzt?
-----------------
Some of the advantages (in my view) over other embedded DSL/fluent interface generators are:
* Very lightweight. The processor library (which only needs to be present during compile time) only depends on [StringTemplate](http://www.stringtemplate.org/), the [Brics Automaton Library](http://www.brics.dk/automaton/) and the [AP Commons Library](https://github.com/misberner/ap-commons). The generated code does not have any dependencies (except for what you use in your implementation class).
* Easy syntax specification through regular expressions
* The complete specification is written in pure Java, no external configuration files (e.g., XML) are required. You can profit from the full editing and content assist capabilities of your IDE.
* Convenience features, such as automatic creation of *varargs* method overloads.


Libraries used by Duzzt
-----------------
*Duzzt* makes use of the following external libraries:
* [StringTemplate](http://www.stringtemplate.org/), for the template-based source code generation
* [Brics Automaton](http://www.brics.dk/automaton/), for converting regular expressions into finite automata
* [AP Commons](https://github.com/misberner/ap-commons/), for easier writing of annotation processors


License
-----------------
*Duzzt* is distributed under the terms of the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).


Developers
-----------------
* [Malte Isberner](https://github.com/misberner)


Links
-----------------
* [API Documentation (Javadoc)](https://misberner.github.com/duzzt/maven-site/apidocs/)
* [Maven Project Site](https://misberner.github.com/duzzt/maven-site/)
