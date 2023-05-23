# Example DSOL Queueing model using discrete events

## Introduction
An M/M/1 queueing system is a system with one server (that's what the 1 stands for in M/M/1), and parts or clients arriving at the server with an exponentially distributed inter-arrival time (the first M denotes that) and an exponentially distributed service time (the second M denotes that). 
For an M/M/1 system to be stable, the average service time, denoted as $\lambda$ should be less than the average inter-arrival time, denoted as $\mu$. The ratio between $\lambda$ and $\mu$ is called $\rho = \lambda / \mu$, and it is also known as the average utilization of the queueing system. 

For a typical queueing system, we are interested in simulating different arrival rates with different service times, and studying the following main output variables:

- queue length (development over time, average, standard deviation, min, max)
- time in queue (development over time, average, standard deviation, min, max)
- time in system (development over time, average, standard deviation, min, max)
- utilization of the server (development over time, average)

If we build a simulation model for this, we need the following types of components:

1. **An entity** that flows through the model
2. **An arrival generator** that creates entities with an interarrival time drawn from the correct distribution
3. **A server** that can serve entities for a given time
4. **A queue** in which waiting entities can be stored
5. **Output statistics objects** to gather the required statistics
6. **Experiment management** including the replication settings and seed management
7. **Random distributions** to draw the inter-arrival time and service times
8. **Input parameters** for the model to set the experimental conditions
9. **A program** that can be started and that creates the simulator, model, and makes it work.
10. **A GUI** to display statistics and graphs to the user


## implementation using event scheduling
Event scheduling is also called 'delayed method invocation'. This means that we can call any method in the model, delayed by a specified duration on an artificial timeline, maintained by the simulation clock. The simulator will take care that all these method calls (also known as **'events'**, are carried out in the right sequence and with the correct simulator time being available. Nothing happens between events, so the simulator jumps from event to event, updating the simulator clock at every event. When multiple events happen at the same time, they can be prioritized; if they have the same priority, they will be handled in a first-come-first-serve manner.

Let's now conceptually discuss each of the three components above:


### 1. An entity that flows through the system
Suppose we have a class called `Entity` that we want to generate. The `Entity` has a `createTime` and an `id`. The `createTime` is useful to calculate the time in system. When we subtract the `entity.createTime` from the simulator time at the moment when the entity leaves the system, we can tally the time in system of that entity for the statistics. 

The simple implementation for such an entity would be:

```java
  protected class Entity
  {
    private final double createTime;
    private final int id;

    public Entity(final int id, final double createTime)
    {
      this.id = id;
      this.createTime = createTime;
    }

    public double getCreateTime() 
    {
      return this.createTime;
    }

    public int getId()
    {
      return this.id;
    }

    @Override
    public String toString()
    {
      return "Entity [createTime=" + this.createTime + ", id=" + this.id + "]";
    }
  }
```


### 2. An arrival generator that makes entities
For the generation of the entities, we create a method called `generate()`, that we call when constructing the model. The `generate` method creates one entity and either offers it to the `startProcess(Entity)` method when the server is idle, or adds it to the rear of the queue when the server is busy. 

After the just created entity has been handled, the `generate()` method calls itself after a time equal to a random value drawn from the Exponential inter-arrival time distribution. In a sense, the method re-schedules itself indefinitely, each time with an inter-arrival time from a given exponential distribution with $\lambda$ as the parameter.

The code to generate an entity would be:

```java
  protected void generate() throws SimRuntimeException
  {
    double time = this.simulator.getSimulatorTime();
    Entity entity = new Entity(this.entityCounter++, time);
    synchronized (this.queue)
    {
      if (this.capacity - this.busy >= 1)
      {
        // process
        this.tallyTimeInQueue.register(0.0); // no waiting
        startProcess(entity);
      }
      else
      {
        // queue
        this.queue.add(new QueueEntry<Entity>(entity, time));
        this.persistentQueueLength.register(time, this.queue.size());
      }
    }
    this.simulator.scheduleEventRel(this.interarrivalTime.draw(), 
        this, "generate", null);
  }
```

A few explanations:

- The `Model` class has a queue called `queue` (see #4), a server capacity called `capacity` (default 1), and a number of entities being processed by the server at this moment called `busy` (0 or 1 in the default situation).
- The method `simulator.getSimulatorTime()` returns the current time of the simulator.
- There is a statistic called `persistentQueueLength` (see #5) that keeps the statistics for the queue length. Every time an entity enters the queue or leaves the queue, the statistic is updated.
- There is a statistic called `tallyTimeInQueue` (see #5) that keeps the statistics for the time the entities spent in the queue. Every time an entity leaves the queue, the statistic is updated. When there is **no** waiting time in the queue, we have to explicitly register this as a zero waiting time. 
- The last line re-schedules the `generate()` method. It indicates that we should schedule after a relative duration (`scheduleEventRel`) and not at an absolute point in time. The interarrival time is drawn from a distribution object (see #6) called `interarrivalTime`. The last three arguments indicate the object instance on which the method will eventually be scheduled (`this`), the name of the method (`generate`) and the arguments to pass to the method (`null`), so no arguments.

!!! Note
    The time stays constant as long as the executed event (method) is busy. If the execution of a method takes 5 minutes on the wall clock (real time), the simulation clock is standing still for that entire duration. On the other hand, if the next event is 1000 years later than the last event, the simulator clock will instantaneously jump 1000 years ahead to execute the next event.

!!! Note
    The execution of the methods is single-threaded. No parallel execution occurs, and the `Simulator` carries out one event at a time, so we do not have to be afraid that another entity is being generated while we did not yet increase the `busy` flag of the server. The next event will only be carried out when the current event, including method calls to other methods, has been completed. 


### 3. A server that can serve the entities for a given time
When the server is free, it is offered a generated entity at some time through the `startProcess(entity)` method. To indicate that the server is busy, we increase the value of the variable `busy` by 1 (this is already a preparation for the so-called M/M/c system where multiple entities can be served at the same time). 

#### startProcess()
The `startProcess(entity)` method of the server calculates some statistics, and releases the entity after the service time by calling the `endProcess(entity)` method. The `startProcess(..)` method looks as follows:

```java
  protected void startProcess(final Entity entity) throws SimRuntimeException
  {
    double time = getSimulator().getSimulatorTime();
    this.busy++;
    this.persistentUtilization.register(time, this.busy);
    this.simulator.scheduleEventRel(this.processingTime.draw(), 
        this, "endProcess", new Object[] {entity});
  }
```

The first three statements of the method body are for updating the utilization statistics. First, the number of entities being processed is increased, then the new utilization is registered in the statistic. 

The fourth statement is scheduling the end of the process; it draws a delay from the `processingTime` distribution, and schedules a call to the method named `this.endProcess` after the delay. The methods expects one argument: the `entity`. In a sense, it is calling the method `this.endProcess(entity)` after the delay. 

#### endProcess()
The `endProcess(entity)` method has to do three things: (1) increasing the capacity of the server, (2) seeing if there are entities waiting in the queue and if yes, removing the first entity from the queue and processing it on the server, and (3) calculating statistics on the service duration. The method looks as folows:

```java
  protected void endProcess(final Entity entity) throws SimRuntimeException
  {
    double time = getSimulator().getSimulatorTime();
    this.busy--;
    this.persistentUtilization.register(time, this.busy);
    if (!this.queue.isEmpty())
    {
        QueueEntry<Entity> queueEntry = this.queue.remove(0); 
        this.persistentQueueLength.register(time, this.queue.size());
        this.tallyTimeInQueue.register(time - queueEntry.getQueueInTime());
        startProcess(queueEntry.getEntity());
    }
    this.tallyTimeInSystem.register(time - entity.getCreateTime());
  }
```

The first three statements are analogous to those in the `process()` method, but instead of decreasing the used capacity, it increases the used capacity by one. Statement 4 checks whether there are elements in the queue. If yes, we remove the first entry from the queue. The statistic for the queue length and the time-in-queue are updated, ather which the removed entity is offered to the `startProcess` method. The last stetement tallies the time-in-system of the entity. Since no statement using the entity comes afterward, the entity is removed from the model after processing.

The `tallyTimeInQueue` statistic registers the value for the time-in-queue statistic, by subtracting the time the entity entered the queue (`queueEntry.getQueueInTime()` from the current simulation time. This is the time that the entity has spent in the queue. 


### 4. A queue in which waiting entities can be stored
In this model, the queue is represented by a java `List`. In theory, it would be sufficient to store the entity in the list with `this.queue.add(entity)`, and remove the first entity from the queue with `Entity entity = this.queue.remove(0)`. When the model gets more complicated, however, and multiple servers with queues are part of the model, the queue should store the time when the entity entered the queue together with the entity itself. This is exactly what we have done in this model:

```java
  protected class QueueEntry<E>
  {
    private final double queueInTime;
    private final E entity;

    public QueueEntry(final E entity, final double queueInTime)
    {
        this.entity = entity;
        this.queueInTime = queueInTime;
    }

    public double getQueueInTime()
    {
        return this.queueInTime;
    }

    public E getEntity()
    {
        return this.entity;
    }

    @Override
    public String toString()
    {
        return "QueueEntry [queueInTime=" + this.queueInTime 
            + ", entity=" + this.entity + "]";
    }
  }
```

The `QueueEntry` stores the entity AND the time when it entered the queue. Thereby, it is easy to determine the duration that the entity spent in the queue when it leaes the queue. The queue is defined as follows in the model:

```java
  private List<QueueEntry<Entity>> queue = new ArrayList<QueueEntry<Entity>>();
```


### 5. Output statistics to store the results
The model defines four output statistics:

```java
  SimTally<Double> tallyTimeInQueue;
  SimTally<Double> tallyTimeInSystem;
  SimPersistent<Double> persistentUtilization;
  SimPersistent<Double> persistentQueueLength;
```

Two of the statistics are tallies, and two of the statistics are persistent, or time weighted, statistics. 

A tally is a statistic for which you register values, and it calculates mean, standard deviation, min, max, and higher order moments by simply using the observations as such. The mean is calculated by:

$$
  \text{mean} = \sum_{i=1}^N{\frac{v_i}{N}}
$$

where $v_i$ are the registered values, and $N$ is the number of registered values. If we register the values 2 and 4, the average is 3.

A persistent statistic is a time-weighted statistic that takes into account how long a certain value persisted. If we offer the value 2 for 10 time units, and the value 4 for 2 time units, the average is (10 * 2 + 2 * 4) / 12 = 2.33. Instead of dividing by the number of observations, we divide over the total time. The mean is calculated by:

$$
  \text{mean} = \int_0^T{\frac{t_i * v_i}{T}}\text{ where }T = \sum_{i=1}^T{t_i}
$$

In this case, $v_i$ are the registered values, and $t_i$ are the durations for which value $v_i$ was registered.

Therefore, a tally only needs a value to be registered:

```java
  this.tallyTimeInQueue.register(time - queueEntry.getQueueInTime());
```

whereas a persistent statistic needs a timestamp AND a new value to be registered:

```java
  this.persistentQueueLength.register(time, this.queue.size());
```

### 6. Experiment management
A simulation model is executed as part of a properly designed experiment. The experiment sets the input parameters, measures the output statistics, and establishes a relationshp between the input paprameters and the calculated indicators. Often, the model is ran multiple times (so-called _replications_ of the simulation run), to avoid any dependency on the particularities of a specific run. When the model is stable, and the indicators easily converge towards the same value, a handful of replications, typically 5 or 10, is sufficient. When the model has rare events that can happen at particular times, such as weather events, failures, or disturbances, many more replications are needed to assess the true value of the output statistics, since the outcome of two replications can differ significantly. In such a case, the 95% confidence interval of the output statistics needs to be calculated and replications have to be repeated until the confidence interval is considered to be small enough for the purpose of the experiment. In this case, we start with observing one replication, but want to extend to multiple replications later.

Two other values that need to be set for the experiment are the length of each run, and the so-called warmup period -- the start period of the model that it needs to cover the transient period. When a model needs time to get realistically 'filled' and therefor show realistic behavior, we remove the first part of the run (that shows irrealistic behavior, e.g., because the model is too 'empty') from the statistics. In other words, the warmup period is the duration after which the statistics are all reset to their initial values. For this queueing model, we set the run time to 1000 time units, and the warmup period to 0, since an empty system is quite realistic, and the system is so simple, it does not need any time to get realistically filled.

In the code, setting up the `Simulator`, `Model` and `Replication` is pretty straightforward:

```java
  protected MM1Application()
  {
    DevsSimulatorInterface<Double> simulator = new DevsSimulator<>("MM1.Simulator");
    DsolModel<Double, DevsSimulatorInterface<Double>> model = new MM1Model(simulator);
    Replication<Double> replication = new SingleReplication<>("rep1", 0.0, 0.0, 1000.0);
    simulator.initialize(model, replication);
    simulator.start();
  }

  public static void main(final String[] args)
  {
    new MM1Application();
  }
```

Explanation:

- We first create a `DevsSimulator`. The time type for the Simulator is `Double` (it could also be, e.g., `Float`, `Integer`, or `Duration`). The Simulator has a name to identify it: "MM1.Simulator". `DevsSimulator` stands for a Discrete Event Simulator -- a simulator that maintains an event list with future events, and that allows for delayed method invocation. Several other types of simulators exist within DSOL.
- The `MM1Model` is created and uses the time type `Double` and the `DevsSimulatorInterface<Double>` as its simulator. These generics are important as they help to strongly type the methods that are part of the model and the simulator.
- The `Replication` is instantiated next as a `SingleReplication` (so this is not an experiment with multiple replications) with name "rep1", starting time 0.0, warmup period 0.0, and run length 1000 time units. 
- The simulator initializes the model with data from the replicaton (timing, seeds, random streams, input variables). 
- The simulator is asked to start the model execution.

!!! Note
    When the simulator receives the `initialize` method call, it first _constructs_ the model by calling the `model.constructModel()` method. In other words, the model carries out all the initial code that is necessary to start executing. Sometimes this is only a few statements of code. In other cases, the entire model is instantiated from a database. The reason for carrrying out the initialization is (a) that thereby the values of input variables can be used to create the structure of the model, and (b) the model is initialized for every replication to its initial state, and there are no 'leftovers' from previous replications.
    
When we would want to carry out 10 replications, the construction of the simulator, model, and experiment would look as follows:

```java
  protected MM1Application()
  {
    this.simulator = new DevsSimulator<Double>("MM1.Simulator");
    this.model = new MM1Model(this.simulator);
    this.experiment = new Experiment<>("mm1", this.simulator, 
        this.model, 0.0, 0.0, 1000.0, 10);
    this.experiment.start();
  }
```

In this case, the creation of the simulator and the model is the same, but now we create an `Experiment` that has the same arguments as the `SingleReplication` class above, but with one extra argument: the number of replications (10). When starting the experiment, it will create the replications one-by-one, initialize the model with the replication, and start the replication. Summary statistics are gathered that can be used after all 10 replications have been completed.


### 7. Random distributions to draw the inter-arrival time and service times
DSOL has a solid system for random number generation (RNG), stochastic distributions, and seed management. 

#### Random number generator
DSOL allows for multiple, independent, random streams to be used in the model, allowing for the usage of so-called _Common Random Numbers_ experiments. The most used random number generator is the _Mersenne Twister_, the de facto standard for RNG in discrete-event simulation. Other RNGs are offered as well, and it is easy to add a special RNG if needed. Random streams are typically offered through DSOL's experiment management, but can also be created by hand:

```java
  private StreamInterface stream = new MersenneTwister(12);
```

where 12 is the (fixed) seed of the RNG. The disadvantage of defining your own RNG is that the seed is not changed between replications, in case the model is ran multiple times to get a confidence interval for the output statistics such as the average time-in-queue or the average utilization of the server. Experiment management (see section 7) takes care of defining random streams that _are_ (reproducibly) changed between replications.

!!! Note
    Proper experimentation with simulation models is all about reproducibility. So, we want some values to be random, but it should be reproducibly random. This means that if we run the model again, with the same seed settings, we should get exactly the same results. Only when these conditions are fulfilled, we can use the simulation to run a proper scientific experiment. Seed management for the RNGs is key in ensuring randomness _and_ reprodicibility.
    
#### Stochastic distributions
A model can define multiple stochastic distributions, by specifying the type of distribution, the parameters for the distribution, and the RNG to be used for drawing the random numbers. DSOL offers both continuous distributions such as Exponential, Triangular, Normal, Uniform, Weibull, Gamma, Beta, Lognormal, and discrete distributions such as Poisson, Discrete Uniform, Bernoulli, and Geometric. A distribution is a class that is instantiated, e.g., as follows for the inter-arrival distribution and the service time distribution:

```java
  private DistContinuous interarrivalTime = new DistExponential(stream, 1.0);
  private DistContinuous processingTime = new DistExponential(stream, 0.9);
```

Drawing a value from such a distribution is done by, e.g., `this.interarrivalTime.draw()`. 

#### Seed management
The `SingleRreplication` example for the RNG is not using seed management, and has the same value for the seed for all replications. That is clearly not what we would want for an experiment with multiple replications. For this use case, the `Experiment` class in DSOL takes care of proper seed management, updating the seed value for each new replication in a reproducible way. You can create as many properly managed random streams in the experiment as needed, but there is always one default stream available in the `Experiment` that you can use. You can access it by:

```java
  StreamInterface defaultStream = getDefaultStream();
```

This stream can now be used in the stochastic distributions for the inter-arrival time and the service time:

```java
  this.interarrivalTime = new DistExponential(defaultStream, 1.0);
  this.processingTime = new DistExponential(defaultStream, 0.9);
```

The streams will automatically be reset with a new seed before a new replication starts. 


### 8. Input parameters for the model to set the experimental conditions
In most cases, an experiment with a simulation model has input values that can be changed, to establish the relationship between the input values on the output statistics. DSOL provides a wide range of input values that can be set and accessed in the model. The input parameters are stored in a hierarchical map, that enables parameters that belong together to be grouped. Many types of parameters exist, such as strings, floating point variables, integers, units, scalar quantities with a unit, values from a list, continuous distributions, discrete distributions, and submaps. 

In the case of the M/M/1 model, we would want to set the parameter values for the exponential distributions for the inter-arrival times and for the service times. We also could set the capacity for the server. In code, this would look as follows:

```java
  this.inputParameterMap.add.add(new InputParameterDouble("intervalTime", 
      "Average interval time", "Average interval time", 1.0, 1.0));
  this.inputParameterMap.add(new InputParameterDouble("serviceTime", 
      "Average service time", "Average service time", 0.8, 2.0));
  this.inputParameterMap.add(new InputParameterDouble("capacity", 
      "Server capacity", "Server capacity", 1.0, 3.0));
```

The arguments of the methods are as follows:

1. key, by which the value can be easily retrieved.
2. short name to display the value, e.g., in a table
3. longer description to explain the input value in, e.g., a GUI. This string may contain HTML markup.
4. default value.
5. sequence number in this map as a floating point value, makig it easy to insert a value without renumbering. If another value would have to be added between the intervalTime and serviceime, it could get 1.5 as its sequence, which would place it between the frst two values for the user interface or display.

To retrieve a set input value, the model can use, e.g., the following code:

```java
  this.interarrivalTime = new DistExponential(defaultStream, 
      (Double) this.inputParameterMap.get("intervalTime").getValue());
  this.processingTime = new DistExponential(defaultStream, 
      (Double) this.inputParameterMap.get("serviceTime").getValue());
```

The call to `parameters.get("generator.intervalTime")` retrieves the parameter _object_ that has `getKey()`, `getDescription()`, `getDefaultValue()` and `getValue()` methods, and several more. Here, we use the `getValue()` method to retrieve the value set by the user, and cast it to a Double.

!!! Note
    When submaps are used, the 'dot'-notation gives access to the parameters. Suppose there are several parameters for the server that are stored in a submap with the key `server`, then the paramters such as the server capacity can be retrieved by `(Integer) this.inputParameterMap.get("server.capacity").getValue()`. This enables the maintainance of a diverse set of input parameters in a comprehensible way.


### 9. A program that can be started to create the model
As shown above, the `Simulator`, `Model`, and `Experiment` can be created in a constructor:

```java
  protected MM1Application()
  {
    this.simulator = new DevsSimulator<Double>("MM1.Simulator");
    this.model = new MM1Model(this.simulator);
    this.experiment = new Experiment<>("mm1", this.simulator, 
        this.model, 0.0, 0.0, 1000.0, 10);
    this.experiment.start();
  }

  public static void main(final String[] args)
  {
    new MM1Application();
  }
```

When we want to print the results of the replication and of the entire experiment, this is not straightforward, since we have no clue when a replication or the experiment has finished. For the replication, there are _two ways_ in which we can print the results at the end of the replication. 

#### Printing results at the end of a replication option #1: simulation event
We can print the results of the output statistics at the end of a replication by scheduling a method on the event list, just before the simulation has ended. E.g., by including the following code in the `constructModel()` method:

```java
  public void constructModel()
  {
    ...
    this.simulator.scheduleEventAbs(this.simulator.getReplication().getRunLength(), 
        this. "reportStats()", null);
  }
  
  protected void reportStats()
  {
    System.out.println("average queue length   = " 
        + this.persistentQueueLength.getWeightedSampleMean());
    System.out.println("average time in queue  = " 
        + this.tallyTimeInQueue.getSampleMean());
    System.out.println("average time in system = " 
        + this.tallyTimeInSystem.getSampleMean());
    System.out.println("average utilization    = " 
        + this.persistentUtilization.getWeightedSampleMean());
  }
```

The replication takes care that **first** all regular events at the time when the replication ends are executed before ending the simulation. If, however, the time is even the slightest bit later, e.g., because of a calculation, the `reportStats()` method might not be scheduled. Since we directly use the value of `getReplication().getRunLength()` here, there is no danger of the method not being executed.

#### Printing results at the end of a replication option #2: pub/sub event
The second way to print the results at the end of the run is to subscribe to an event of the `Simulator` that indicates the end of the replication, called `Replication.END_REPLICATION_EVENT`. The subscription is done as follows (e.g., in the setup of the simulator, model and experiment):

```java
  class MM1Application implements EventListener
  {
    protected MM1Application()
    {
      this.simulator = new DevsSimulator<Double>("MM1.Simulator");
      this.model = new MM1Model(this.simulator);
      this.experiment = new Experiment<>("mm1", this.simulator, 
          this.model, 0.0, 0.0, 1000.0, 10);
      this.experiment.addListener(this, Replication.END_REPLICATION_EVENT);
      this.experiment.start();
    }
```

When the end of the replication is reached, the `notify(event)` method is called. We can then print the results if the event is indeed the `END_REPLICATION_EVENT`:

```java
  @Override
  public void notify(final Event event) throws RemoteException
  {
    if (event.getType().equals(Replication.END_REPLICATION_EVENT))
    {
      reportStats();
    }
  }
```

#### printing results at the end of an experiment
Printing the results at the end of an entire experiment can only be done with the publish/subscribe method shown above, but then for the `Experiment.END_EXPERIMENT_EVENT` event. The code would look, e.g., as follows (also inclduing the `Replication.END_REPLICATION_EVENT`:

```java
  class MM1Application implements EventListener
  {
    protected MM1Application()
    {
      this.simulator = new DevsSimulator<Double>("MM1.Simulator");
      this.model = new MM1Model(this.simulator);
      this.experiment = new Experiment<>("mm1", this.simulator, 
          this.model, 0.0, 0.0, 1000.0, 10);
      this.experiment.addListener(this, Experiment.END_EXPERIMENT_EVENT);
      this.experiment.addListener(this, Replication.END_REPLICATION_EVENT);
      this.experiment.start();
    }
    
    @Override
    public void notify(final Event event) throws RemoteException
    {
      if (event.getType().equals(Replication.END_REPLICATION_EVENT))
      {
        reportStats();
      }
      if (event.getType().equals(Experiment.END_EXPERIMENT_EVENT))
      {
        reportFinalStats();
      }
    }
    
    protected reportFinalStats()
    {
      System.out.println("Final statistics:");
      SortedMap<String, SortedMap<String, Tally>> stats 
          = this.experiment.getSummaryStatistics();
      for (String statMapKey : stats.keySet())
      {
        System.out.println("\nSummary statistic for: " + statMapKey);
        System.out.println(Tally.reportHeader());
        SortedMap<String, Tally> statMap = stats.get(statMapKey);
        for (String statKey : statMap.keySet())
        {
          Tally stat = statMap.get(statKey);
          System.out.println(stat.reportLine());
        }
        System.out.println(Tally.reportFooter());
      }
    }
```

The `reportFinalStats()` method makes use of the fact that the _summary statistics_ over the 10 replications are _all_ Tally statistics (all statistics values are the normal average over 10 runs). The Tally has a nice property that it can report the results in nice lines with a header and a footer.

It will report, e.g. the following (small excerpt of the summary statistics):

```text
Summary statistic for: Time in Queue
--------------------------------------------------------------------------------------------------
| Tally name                        |      n |       mean |     st.dev |    minimum |    maximum |
--------------------------------------------------------------------------------------------------
| Max                               |     10 |      5.607 |      1.595 |      3.477 |      9.591 |
| Min                               |     10 |      0.000 |      0.000 |      0.000 |      0.000 |
| N                                 |     10 |   1007.300 |     26.702 |    956.000 |   1047.000 |
| PopulationExcessKurtosis          |     10 |      8.286 |      5.518 |      2.627 |     22.921 |
| PopulationKurtosis                |     10 |     11.286 |      5.518 |      5.627 |     25.921 |
| PopulationMean                    |     10 |      0.507 |      0.074 |      0.382 |      0.684 |
| PopulationSkewness                |     10 |      2.560 |      0.652 |      1.808 |      4.142 |
| PopulationStDev                   |     10 |      0.874 |      0.135 |      0.622 |      1.113 |
| PopulationVariance                |     10 |      0.783 |      0.238 |      0.387 |      1.238 |
| SampleExcessKurtosis              |     10 |      8.333 |      5.547 |      2.645 |     23.045 |
| SampleKurtosis                    |     10 |     11.274 |      5.512 |      5.621 |     25.894 |
| SampleMean                        |     10 |      0.507 |      0.074 |      0.382 |      0.684 |
| SampleSkewness                    |     10 |      2.564 |      0.653 |      1.811 |      4.148 |
| SampleStDev                       |     10 |      0.875 |      0.135 |      0.622 |      1.113 |
| SampleVariance                    |     10 |      0.783 |      0.238 |      0.387 |      1.239 |
| Sum                               |     10 |    511.195 |     79.132 |    386.626 |    705.454 |
--------------------------------------------------------------------------------------------------
```

Tables like this will also be printed for the queue length, server utilization, and time in system.



### 10. A GUI to display statistics and graphs to the user


