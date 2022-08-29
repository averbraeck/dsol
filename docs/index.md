# DSOL - Distributed Simulation Object Library

## DSOL Introduction

DSOL is a library, or a set of libraries, to build, experiment with, and analyze simulation models.
The design of the library is rooted in simulation theory, systems thinking, and management science. 
The software design is modular, and adheres to the principles of object-oriented programming. 
The software allows for building simulation models in different formalisms, such as discrete-event
simulation, differential equations, agent-based modeling, atomic DEVS, and hierarchical DEVS. 
Different simulation formalisms can co-exist in a single model. The word 'distributed' in DSOL 
indicates that the design of the software has taken distributed models as a starting point, rather 
than as an afterthought. The world 'library' indicates that DSOL is not a 'drag-and-drop' end-user
tool, but rather a programming library to build computer simulation models. Two implementations
exist: a Java version [https://github.com/averbraeck/dsol4](https://github.com/averbraeck/dsol4) and
a python version [https://github.com/averbraeck/pydsol-core](https://github.com/averbraeck/pydsol-core).
Domain specific libraries can be built on top of these general-purpose simulation libraries, e.g., 
for traffic modeling ([OpenTrafficSim](https://github.com/averbraeck/opentrafficsim2)), 
disease spread ([MedLabs](https://github.com/averbraeck/medlabs)) or supply chains (forthcoming). 


## For whom is this manual intended?

This manual explains and demonstrates the fundamentals of creating simulation models with DSOL. It provides a straightforward explanation of
the technology, Java classes, interfaces, simulation models, output statistics, etc. In addition, it contains a deep dive into the advanced usage of DSOL, such as multi-formalism simulation, multi-level simulation, agent-based simulation, embedded simulation, and parallel and distributed simulation with DSOL. 

Although this manual aims to provide a primer, it’s not written for dummies. Readers are expected to have an understanding of simulation and the latest version of Java. Before reading this tutorial, you should be fluent in the Java programming language (version 8 or up) and have some practical understanding of simulation. If you are unfamiliar with the Java programming language, we recommend you to read _Thinking in Java, 4th Edition_ (Eckel, 2006) or _Head First Java, 2nd Edition_ (Sierra & Bates, 2005). DSOL 3.0 makes heavy use of generics and collections, therefore the _Java Generics and Collections_ book (Naftalin & Wadler, 2007) is a good resource. For more advanced Java, Joshua Bloch’s _Effective Java, 3rd Edition_ (Bloch, 2018) is a great help. If you are completely unfamiliar with the concept of simulation we recommend you to study the _Handbook of Simulation_ (Banks, 1998) or _Simulation modeling and analysis, 5th Edition_ (Law & Kelton, 2014).


## Resources

DSOL, as of version 4.0 can be downloaded from github, and ready-to-use jars can be included from Maven Central. The following sites are used:

* [https://github.com/averbraeck/dsol4](https://github.com/averbraeck/dsol4) for the latest version of the sources.
* [https://simulation.tudelft.nl/dsol/manual](https://simulation.tudelft.nl/dsol/manual) for the manual.
* the manual can also be found at [https://dsol4.readthedocs.io](https://dsol4.readthedocs.io).
* [https://simulation.tudelft.nl/dsol/docs/latest](https://simulation.tudelft.nl/dsol/docs/latest) for the technical documentation.
* [https://search.maven.org/search?q=nl.tudelft.simulation](https://search.maven.org/search?q=nl.tudelft.simulation) for the Maven jars of DSOL.
* the Maven Jars are also accessible via [https://mvnrepository.com/search?q=nl.tudelft.simulation](https://mvnrepository.com/search?q=nl.tudelft.simulation).

Furthermore, Java itself, a development environment, and resources on which DSOL is dependent are necessary:

* Java, as of version 11. DSOL 4 and higher can not be used with prior versions of the Java programming language. For developing models with DSOL, we recommend to use a Java Development Kit (JDK), rather than a JRE. For running models, a JRE is sufficient. Java can be downloaded from Oracle or as an open source build: [https://jdk.java.net/archive/](https://jdk.java.net/archive/) for Java-11.

* We highly recommend using an integrated development environment for the construction of simulation models. This tutorial refers to the Eclipse development environment (version 2022-06 or higher) which can be downloaded from [https://www.eclipse.org](https://www.eclipse.org).

* Libraries on which DSOL is dependent. DSOL makes use of several libraries, such as djunits to work with Time and Durations, djutils for data analytics and statistics, and tinylog for logging. The interactive version of DSOL in dsol-swing makes, amongst others, use of jfreechart for graphs. The dependencies will be automatically resolved when using an environment like Maven (or gradle).


## Open Source character of DSOL

We want as many people and organizations as possible to be able to make use of DSOL. Therefore, the license of DSOL is a BSD 3-clause license, which states that DSOL can be used for any purpose, public or commercial, where TU Delft is not responsible or accountable in any way for the correct working of the software. All sources of DSOL are provided on the Maven website and through SVN. DSOL is only dependent on packages that have a license that is equally open, such as the MIT license, BSD license, Apache license, or Eclipse license. We use libraries with the LGPL (Library GPL or Lesser GPL license), but not with the GPL license, as that would make commercial use and commercial extensions of DSOL more difficult.


## References

* Banks, J. (Ed.). (1998). _Handbook of simulation : Principles, methodology, advances, applications, and practice_. New York, NY: Interscience.
* Bloch, J. (2018). _Effective Java (3rd ed.)_. Boston, MA: Addison-Wesley.
* Eckel, B. (2006). _Thinking in Java (4th ed.)_. Upper Saddle River, NJ: Prentice-Hall.
* Jacobs, P. H. M. (2005). _The DSOL simulation suite_. PhD thesis, Delft University of Technology.
* Jacobs, P. H. M., Lang, N. A., & Verbraeck, A. (2002). [DSOL: A distributed Java based discrete event simulation architecture](https://www.informs-sim.org/wsc02papers/102.pdf). In [_Proceedings of the 2002 Winter Simulation Conference._](https://www.informs-sim.org/wsc02papers/prog02.htm) New York, NY: ACM Press.
* Law, A. M., & Kelton, W. D. (2014). _Simulation modeling and analysis (5th ed.)_. New York, NY: Mc Graw-Hill.
* Naftalin, M., & Wadler, P. (2007). _Java generics and collections_. Sebastopol, CA: O’Reilly.
* Sierra, K., & Bates, B. (2005). _Head first Java (2nd ed.)_. Sebastopol, CA: O’Reilly.
