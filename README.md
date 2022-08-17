# DSOL - Distributed Simulation Object Library (version 4)

## DSOL Introduction

DSOL is a library, or a set of libraries, to build, experiment with, and analyze simulation models.
The design of the library is rooted in simulation theory, systems thinking, and management science. 
The four words of the "Distributed Simulation Object Library" have been chosen purposefully:

* **Distributed**. The word 'distributed' in DSOL indicates that the design of the software has 
  taken distributed models and distributed services as a starting point, rather than as an afterthought. 
  The implementations make heavy use of the so-called publish-subscribe mechanism, allowing flexible 
  interaction between (remote) components.
* **Simulation**: The software allows for building simulation models in different formalisms, such 
  as discrete-event simulation, differential equations, agent-based modeling, atomic DEVS, and 
  hierarchical DEVS. Different simulation formalisms can co-exist in a single model. 
* **Object**: The software design is modular, and adheres to the principles of object-oriented 
  programming. This means (amongst others) that much emphasis is placed on interfaces for simulation 
  services that can be implemented in different ways, and that the inner working of services are
  shielded to avoid breaking them by accident. Extensibility through inheritance and composition 
  through modularity mean that the object orientation is not a limiting factor.
* **Library**: The world 'library' indicates that DSOL is not a 'drag-and-drop' end-user
  tool, but rather a programming library to build computer simulation models. Two implementations
  exist: a Java version [https://github.com/averbraeck/dsol4](https://github.com/averbraeck/dsol4) and
  a python version [https://github.com/averbraeck/pydsol-core](https://github.com/averbraeck/pydsol-core).
  Domain specific libraries can be built on top of these general-purpose simulation libraries, e.g., 
  for traffic modeling ([OpenTrafficSim](https://github.com/averbraeck/opentrafficsim2)), 
  disease spread ([MedLabs](https://github.com/averbraeck/medlabs)) or supply chains (forthcoming). 
  

