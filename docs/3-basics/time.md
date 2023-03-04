# Time in DSOL

## Time and state

Simulation is all about experimenting with a model where **state** changes over **time**. State is easily represented in an object-oriented programming environment: all attribute values of all object instances in the model constitute the state of the model. Programming languages have no similar concept for 'time', though. Therefore time is kept by the Simulator object that is responsible for maintaining and updating the simulation time. 


## Time representation in DSOL

DSOL supports any representation of time that extends Number and that is comparable to other times. The definition of the `SimulatorInterface` class is, for instance:

```java
  public interface SimulatorInterface<T extends Number & Comparable<T>>
```

Examples of common and valid simulation times are:

| Class  | Explanation / Example |
| -----  | -----                 |
| Double | This is the most common simulation time, represented by a double value. |
| Float  | Sometimes used when many simulation events with time need to be stored; a `float` is half the number of bytes of a `double` |
| Long   | Times are both represented by a long value. This is, for instance, a time unit that can be used for agent-based models that use equidistant time 'ticks' rather than varying time intervals between simulation events. |
| [Duration](https://djunits.org/docs/current/apidocs/org/djunits/value/vdouble/scalar/Duration.html) | Duration can be used with its [DurationUnit](https://djunits.org/docs/current/apidocs/org/djunits/unit/DurationUnit.html). An example is <br> `Duration duration = new Duration(45.0, DurationUnit.SECOND);` |
| [FloatDuration](https://djunits.org/docs/current/apidocs/org/djunits/value/vfloat/scalar/FloatDuration.html) | FloatDuration can be used with its [DurationUnit](https://djunits.org/docs/current/apidocs/org/djunits/unit/DurationUnit.html). An example is <br> `FloatDuration duration = new FloatDuration(12.0f, DurationUnit.HOUR);` |

Classes using time in DSOL such as the `Simulator`, `Experiment`, `Replication`, `DSOLModel`, etc., all need to use the same definition of time within one simulation model; see the next section for several of the classes that are defined with a time generic.


## Usage of simulation time type in other classes

Examples of classes and interfaces that use simulation time types as a generic are:

* [**DSOLModel**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/model/DSOLModel.html). The `DSOLModel` is the interface that specifies what a simulation model should implement. An example of a time-specified `DSOLModel` interface is `DSOLModel<Duration>`.
* [**AbstractDSOLModel**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/model/AbstractDSOLModel.html). The `AbstractDSOLModel` is a reference base implementation of the `DSOLModel` interface, that actual models can inherit from. An example is: `public class MM1Model extends AbstractDSOLModel<Double>`
* [**Simulator**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/simulators/Simulator.html). The `Simulator` interface specifies what any DSOL Simulator should implement. Example methods are `start()`, `step()` and `stop()`, as well as the `getSimulatorTime()` method that returns the simulation time. When calling `getSimulatorTime()` on a `Simulator<Duration>`, a `Duration` object is returned, whereas calling the same method on a `Simulator<Double>` returns a double value. All sub-interfaces and implementations of the `Simulator` interface such as the `DEVSSimulator`, the `DESSSimulator`, the `DEVDEVSSimulator` and the `DEVSRealTimeAnimator` have the same time type generic. A discrete-event model without animation using Duration for its time would run on a `DEVSSimulator<Duration>`. 
* [**SimEventInterface**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/formalisms/eventscheduling/SimEventInterface.html) and [**SimEvent**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/formalisms/eventscheduling/SimEvent.html) that represent discrete events in the simulaton, need to know on which time unit they are executed because of the execution time of the event.
* [**Experiment**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/experiment/Experiment.html), [**RunControl**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/experiment/RunControl.html), and [**Replication**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/experiment/ReplicationInterface.html) that represent the run control conditions under which the simulation is executed. A `DSOLModel<Long>` can only be executed by an `Experiment<Long>`.
* Flow-control blocks such as [**Generator**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/formalisms/flow/Generator.html), [**Seize**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/formalisms/flow/Seize.html), [**Delay**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/formalisms/flow/Delay.html), [**Release**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/formalisms/flow/Release.html), [**Duplicate**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/formalisms/flow/Duplicate.html), [**Schedule**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/formalisms/flow/Schedule.html) and [**Departure**](https://simulation.tudelft.nl/dsol/docs/latest/dsol-core/apidocs/nl/tudelft/simulation/dsol/formalisms/flow/Departure.html) all are generics with a time representing class.

