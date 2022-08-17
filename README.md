# DSOL - Distributed Simulation Object Library (version 4)

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
for traffic modeling ([https://github.com/averbraeck/opentrafficsim1](OpenTrafficSim)), 
disease spread ([https://github.com/averbraeck/medlabs](MedLabs)) or supply chains (forthcoming). 

