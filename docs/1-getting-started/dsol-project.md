# Structure of a DSOL project

## Theoretical background

In the [Multi Actor Systems Department](https://www.tudelft.nl/en/tbm/about-the-faculty/departments/multi-actor-systems/) of the [Faculty of Technology, Policy and Management](https://www.tudelft.nl/en/tpm/about-the-faculty/) of [Delft University of Technology](https://www.tudelft.nl/en/), simulation is considered to be more than a tool. Simulation is seen as a method of inquiry which supports decision makers in the generation and evaluation of scenarios to deal with ill-structured problems (Sol, 1982). Examples of ill-structured problems are infrastructure planning, supply chain coordination, coordination of business processes, etc. (Keen & Sol, 2008). 

Simulation as a method of inquiry embodies a sequence of steps (Banks, 1998). First of all a problem situation is conceptualized. Conceptualizing starts with the definition of a system under investigation. The system is the selected set of objects we choose to take into account for further analysis. Conceptualization results in several models of which the object-oriented class diagrams and process oriented IDEF-0 diagrams are typical examples. 

The second step is to specify the system under consideration. In order to provide future what-if scenarios we specify the (potential) state changes of the system as a function of time (Nance, 1981). These state changes can either be continuous or discrete which leads to either continuous or discrete simulation (Zeigler, Praehofer, & Kim, 2000). 

The third step is to design an experiment for our specified simulation model. In this experiment, we will need to specify the different scenarios (treatments) for the experiment and provide all run control parameters (run length, replications, etc.). 

After selecting an appropriate simulator, we execute the experiment and collect all the statistics. In order to both present the outcome of a simulation model, and to support validation and verification, animation is considered as a valuable asset. DSOL supports distributed 2D and text-based animation, as well as 3D animation.


## What is special about DSOL?

The hypothesis underlying the development of DSOL is that the web-enabled era has provided us with the tools and techniques to specify the concepts of simulation in a set of loosely-coupled, web-enabled services. An underlying notion on the quality of most current simulation tools is that they lack
* **usefulness** with respect to their embedded knowledge of the system under investigation. It is too difficult to link simulation tools to underlying transactional information systems.
* **usability** with respect to coordination of distributed conceptualization and specification of models representing the system under investigation.
* **usage** with respect to their integration and use within (portalled) organizational management information systems.

Besides the theoretical requirements that hold for every simulation environment, this has resulted in the following additional requirements for DSOL:
* _Multiple stakeholders_ should be supported over the internet;
* _Multiple model formalisms_ should be supported;
* _Multiple experts_ should be simultaneously be supported in the specification of models;
* _Multiple external support services_ (reporting, databases) should be easily linked to the simulation services;
* _Multiple processors_ should be usable for the execution of experiments.


## Services not included in DSOL

For now, DSOL supports all activities and phases in this method of inquiry but conceptualization. DSOL therefore does not contain any modeling services or tools. You will have to stick to the whiteboard, Visio&trade;, or other UML tools or drawing tools. First steps to create an Eclipse-based conceptual modeling framework based on DSOL have been described by (Çetinkaya, 2013). Her Model Driven Architecture framework called MDD4MS supports model continuity, which means that conceptual models developed with MDD4MS have a well-defined transformation path from conceptual model to a running DSOL model (Çetinkaya, Verbraeck, & Seck, 2015). In addition, (Huang, 2013) describes possibilities to generate DSOL simulation models automatically from data sources.


## Anatomy of DSOL

The following services make up the DSOL suite for simulation:

* **[dsol-base](https://simulation.tudelft.nl/dsol/docs/latest/dsol-base/index.html)**: Base services that all other DSOL packages are dependent on. Examples are logging, concurrency, and several tools for reflection and filtering. dsol-base is only dependent on java-3d to make sure that simulation objects can be `Locatable`, which means that they can have a position in 3D-space.

* **[dsol-naming](https://simulation.tudelft.nl/dsol/docs/latest/dsol-naming/index.html)**: This service links DSOL to the so-called JNDI framework. The Java Naming and Directory Interface (JNDI) provides Java technology-enabled applications with a unified interface to multiple naming and directory services. In these naming and directory services model objects and statistical output can be stored, retrieved, and shared. The dsol-naming project is dependent on dsol-event (and thus on dsol-base). 

* **[dsol-core](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/index.html)**: The core service providing a set of interfaces and classes for simulation. In the dsol-core project you will find various time mechanisms, simulator types, and model formalisms. In addition, a set of continuous and discrete distribution functions and rando number generators are provided, as well as ordinary differential equations for continuous simulation models. Reference inplementations for models and simulation applications are provided, making it quite easy to create a simulation program in Java. dsol-core is dependent on dsol-naming and several other libraries. Console-based or embedded simulations are typically dependent on the dsol-core project.

* **[dsol-animation](https://simulation.tudelft.nl/dsol/docs/latest/dsol-animation/index.html)**: The core classes shared between different animation implementations are part of the dsol-animation project. Examples are core classes for animation and graphs. The dsol-swing project and the dsol-web project are both dependent on dsol-animation.

* **[dsol-swing](https://simulation.tudelft.nl/dsol/docs/latest/dsol-swing/index.html)**: A graphical user interface to be used with the DSOL suite for simulation. Swing dependent animation, graphs, and experimentation can be found in the dsol-swing project. Interactive simulations are typically dependent on the dsol-swing project.

* **[dsol-web](https://simulation.tudelft.nl/dsol/docs/latest/dsol-web/index.html)**: A graphical user interface to be used with the DSOL suite for simulation. Web-enabled (HTML5) animation, graphs, and experimentation can be found in the dsol-web project. Remote interactive simulations are typically dependent on the dsol-web project.

* **[dsol-demo](https://simulation.tudelft.nl/dsol/docs/latest/dsol-demo/index.html)**: Several examples and tutorial and reference models for discrete, continuous and multi-formalism models. Typically, dsol-demo is not linked to any project, but rather used to learn from for the implementation of own models.

Several special projects are:

* **[dsol-introspection](https://simulation.tudelft.nl/dsol/docs/latest/dsol-introspection/index.html)**: This is a 'helper' project used for so-called 'introspection' of simulation objects. Introspection means that the user can look into the attributes of the object and see how they change over time when the simulaton runs.


## Linkage of the right project with Maven

Typically, either _[dsol-core](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/index.html)_ (for command-line based simulations), _[dsol-swing](https://simulation.tudelft.nl/dsol/docs/latest/dsol-swing/index.html)_ (for interactive simulations) or _[dsol-web](https://simulation.tudelft.nl/dsol/docs/latest/dsol-web/index.html)_ (for remote, web-enabled simulations) is linked to a user's project with Maven. Maven will take care that all other dependencies are resolved, with exactly the right versions. The only version that the simulation model builder needs to provide is the DSOL version to use. For the latest version, see the documentation at [https://simulation.tudelft.nl/dsol/docs/latest/latest/](https://simulation.tudelft.nl/dsol/docs/latest/latest/). At the top-right of the screen, the version number of the latest stable version of DSOL is shown.

In the Maven pom-file of the simulation project, add the following dependency for a console-based simulation:

```xml
  <dependencies>
    <dependency>
      <groupId>dsol</groupId>
      <artifactId>dsol-core</artifactId>
      <version>4.2.0</version>
    </dependency>
  </dependencies>
```

And for a swing-based interactive simulation, make the following dependency:

```xml
  <dependencies>
    <dependency>
      <groupId>dsol</groupId>
      <artifactId>dsol-swing</artifactId>
      <version>4.2.0</version>
    </dependency>
  </dependencies>
```


Suppose that we have a project that is dependent on `dsol-core` version `4.2.0`. In that case, the entire `pom.xml` file looks as follows:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  
  <groupId>dsol</groupId>
  <artifactId>dsol-tutorial</artifactId>
  <version>0.1</version>
  <name>DSOL Tutorial Examples</name>
  <description>DSOL Tutorial Examples</description>
  
  <properties>
    <jdk.version>11</jdk.version>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
  </properties>
  
  <dependencies>
    <dependency>
      <groupId>dsol</groupId>
      <artifactId>dsol-core</artifactId>
      <version>4.2.0</version>
    </dependency>
  </dependencies>

</project>
```

Now we are ready to start coding our first example.


## References

* Banks, J. (Ed.). (1998). _Handbook of simulation : Principles, methodology, advances, applications, and practice_. New York, NY: Interscience.
* Çetinkaya, D. (2013). _[Model driven development of simulation models: Defining and transforming conceptual models into simulation models by using metamodels and model transformations](https://repository.tudelft.nl/islandora/object/uuid:3db45913-1662-429f-a385-ed53f5ac41fd/datastream/OBJ/download)._ PhD thesis, Delft University of Technology.
* Çetinkaya, D., Verbraeck, A., & Seck, M. D. (2015). [Model continuity in discrete event simulation: A framework for model-driven development of simulation models](https://repository.tudelft.nl/islandora/object/uuid:d8e21519-9b0c-490f-b1b6-2f39ac647428/datastream/OBJ/download). _ACM Transactions on Modeling and Computer Simulation_, **25**(3), 17:1–17:24. Available from [http://doi.acm.org/10.1145/2699714](http://doi.acm.org/10.1145/2699714).
* Huang, Y. (2013). _[Automated simulation model generation](https://repository.tudelft.nl/islandora/object/uuid:dab2b000-eba3-42ee-8eab-b4840f711e37/datastream/OBJ/download)_. PhD thesis, Delft University of Technology.
* Keen, P. W. G., & Sol, H. G. (2008). _Decision enhancement services: Rehearsing the future for decisions that matter_. Amsterdam, The Netherlands: IOS Press.
* Sol, H. G. (1982). _Simulation in information systems development_. PhD thesis, Rijksuniversiteit Groningen, Groningen, the Netherlands.
* Nance, R. E. (1981). The time and state relationships in simulation modeling. _Communications of the ACM_, **24**(4), 173–179.
* Zeigler, B. P., Praehofer, H., & Kim, T. G. (2000). _Theory of modeling and simulation (2nd ed.)_. San Diego, CA: Academic Press.