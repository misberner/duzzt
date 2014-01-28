Duzzt
================
An Annotation-based Embedded DSL Generator for Java

What is Duzzt?
-----------------
*Duzzt* is a Java Annotation Processor (library) to easily generate *Embedded DSLs* for Java. An Embedded DSL (or EDSL) is a [fluent interface](https://en.wikipedia.org/wiki/Fluent_interface) which follows a certain syntax, i.e., rules that describe legal sequences of method invocation chains. In *Duzzt*, [regular expressions](https://en.wikipedia.org/wiki/Regular_expression) are used to define the syntax of embedded DSLs.


Using Duzzt
-----------------
The steps required to use *Duzzt* naturally depend on which build system you are using.

#### Using Maven
The easiest way to use *Duzzt* is when using [Apache Maven](http://maven.apache.org). Simply add the following snippet to your `<dependencies>` section:
```
<dependency>
    <groupId>com.github.misberner.duzzt</groupId>
    <artifactId>duzzt-processor</artifactId>
    <version>0.0.2</version>
    <!-- This will make sure the dependency is non-transitive! -->
    <scope>provided</scope>
</dependency>
```

#### Manual Setup
If you are not using Maven (and do not use a build system with a compatible dependency management, such as [Apache Ivy](http://ant.apache.org/ivy/), you need the following libraries to be present in your classpath **during compile time** (for the latest stable version 0.0.1):
* [duzzt-annotations-0.0.1.jar](http://repo1.maven.org/maven2/com/github/misberner/duzzt/duzzt-annotations/0.0.1/duzzt-annotations-0.0.1.jar), containing the annotations used by Duzzt
* [duzzt-processor-0.0.1.jar](http://repo1.maven.org/maven2/com/github/misberner/duzzt/duzzt-processor/0.0.1/duzzt-processor-0.0.1.jar), containing the actual Duzzt annotation processor
* [ST-4.0.7.jar](http://www.stringtemplate.org/download/ST-4.0.7.jar), the [StringTemplate](http://www.stringtemplate.org/) library
* [automaton-1.11-8.jar](http://repo1.maven.org/maven2/dk/brics/automaton/automaton/1.11-8/automaton-1.11-8.jar), the [Brics Automaton](http://www.brics.dk/automaton/) library
* [ap-commons-0.0.1.jar](http://repo1.maven.org/maven2/com/github/misberner/ap-commons/0.0.1/ap-commons-0.0.1.jar), a commons library for annotation processing

Note that **none** of these libraries is required during runtime of your program.

Getting Started
-----------------
Check the [examples](https://github.com/misberner/duzzt/tree/master/examples/src/main/java/com/github/misberner/duzzt/examples) in the Git repository for some usage examples, or the Wiki for a [tutorial introduction](https://github.com/misberner/duzzt/wiki/A-Tutorial-Introduction-to-Duzzt). Or take a look at the Javadoc of the main annotation, [`@GenerateEmbeddedDSL`](https://misberner.github.com/duzzt/maven-site/0.0.1/apidocs/com/github/misberner/duzzt/annotations/GenerateEmbeddedDSL.html).


Why Duzzt?
-----------------
Some of the advantages (in my view) over other embedded DSL/fluent interface generators are:
* Very lightweight. The processor library (which only needs to be present during compile time) only depends on [StringTemplate](http://www.stringtemplate.org/), the [Brics Automaton Library](http://www.brics.dk/automaton/) and the [AP Commons Library](https://github.com/misberner/ap-commons). The generated code does not have any dependencies (except for what you use in your implementation class).
* Easy syntax specification through regular expressions.
* The complete specification is written in pure Java, no external configuration files (e.g., XML) are required. You can profit from the full editing and content assist capabilities of your IDE.
* Convenience features, such as automatic creation of *varargs* method overloads.
* Highly configurable, but minimum configuration sufficient for many applications


Libraries used by Duzzt
-----------------
*Duzzt* makes use of the following external libraries:
* [StringTemplate](http://www.stringtemplate.org/) (BSD License), for the template-based source code generation
* [Brics Automaton](http://www.brics.dk/automaton/) (BSD License), for converting regular expressions into finite automata
* [AP Commons](https://github.com/misberner/ap-commons/) (Apache License), for easier writing of annotation processors


License
-----------------
*Duzzt* is distributed under the terms of the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).


Developers
-----------------
* [Malte Isberner](https://github.com/misberner)

Links
-----------------
* API Documentation (Javadoc): release | [snapshot](https://misberner.github.io/duzzt/maven-site/0.0.1-SNAPSHOT/apidocs/)
* Maven Project Site: release | [snapshot](https://misberner.github.io/duzzt/maven-site/0.0.1-SNAPSHOT)
