# Output Statistics

## Statistics classes

DSOL provides three standard statistics classes: the `Counter`, the `Tally`, and the `Persistent`. The classes have the following functions:

* the **Counter** provides a count of something happening in the model we are interested in. An example is the number of a certain type of events that happened, e.g. the number of entities processed or the number of entities created. 
* the **Tally** provides base unweighed statistics of the observations that are given, such as average, standard deviation and number of observations. 
* the **Persistent** provides time-weighed statistics of the (time, observation) pairs that are given. The time-weighed average takes into account how long each value has held a certain value. An example is queue length: when the queue was 10 minutes of length 1, and 5 minutes of length 2, the average queue length is not 1.5 but (10 x 1 + 5 x 2) / 15 = 1.33.

The classes have the following relations:

![](jstats-statistics.png)


## Use of events in statistics classes

The diagram above shows that all statistics objects are both an `EventProducer` and an `EventListener`. The observations are given to the ststistics objects using events, and the statistics produce events that the status has changed after receiving events. This enables loose coupling between the model, statistics objects, and user interfaces showing the current state of the statistics objects. Using the publish-subscribe mechanism (see [Advanced Topics](../../advanced/pub-sub) for the inner working of the pub-sub mechanism), statistics objects are kept up-to-date with the latest observations.

Suppose that a `DSOLModel` wants to keep a `Tally` object updated about the waiting time of an entity. The `AbstractDSOLModel` is already an `EventProducer`. This means that it can already fire events to listeners. When the model is instantiated, it can either make the statistics classes itself, or allow external code (e.g., the `public static void main` method) to make the stastistics class for the model. Suppose the model makes the `Tally` object itself in the constructor, the following code shows how to arrange the subscription:

```java
public class QueueModel extends AbstractDSOLModel.TimeDoubleUnit<SimulatorInterface.TimeDoubleUnit>
{
    public static final EventType WAITING_TIME_EVENT = new EventType("WAITING_TIME");

    public QueueModel(final SimulatorInterface.TimeDoubleUnit simulator)
    {
        super(simulator);
        Tally tally = new Tally("Waiting time");
    }

    public void constructModel() throws SimRuntimeException
    {
        // ...
        fireEvent(WAITING_TIME_EVENT, waitingTime);
        // ...
    }
}
```

The loose coupling means that the model does not need to know that a Tally is listening to the `WAITING_TIME_EVENT`. It could also be a completely different object, or multiple Tallies and/or other objects that listen to the waiting time observation. A `Persistent` listener expects a `TimedEvent` instead of an `Event`. Use the `fireTimedEvent` method rather than the `fireEvent` method in case of possible Persistent listeners (or other listeners that are interested in the time of the observation). 

When elsewhere in the code we are interested in the mean of the tallied waiting times, we can use the following code (suppose that the pointer to the `Tally` object is stored in `this.waitingTimeTally`):

```java
System.out.println("Average waiting time: " + this.waitingTimeTally.getSampleMean());
```


## Simulation-aware statistics classes

the `Counter`, the `Tally`, and the `Persistent` classes have been extended into `SimCounter`, `SimTally`, and `SimPersistent` to provide simulation-aware versions of the base statistics classes. The fact they are simulation-aware has a couple of advantages:

1. They automatically subscribe to the Simulator's `WARMUP_EVENT` and `END_REPLICATION_EVENT`. Statistics are cleared when the warmup time has been reached, and at the end of the replication, results of the statistics objects are stored.
2. They are registered in the so-called `Context` of the `Simulator`. This is a hierarchical JNDI storage area that is accessible from other code and can be used to access statistics objects from other applications.
3. The `TimedEvent` of the `SimPersistent` is aware of the exact type of absolute time that the simulator uses, which can be used to calculate time intervals based on the absolute time type.


