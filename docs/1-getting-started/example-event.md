# Example DSOL Queueing model using discrete events

## Introduction
An M/M/1 queueing system is a system with one server (that's what the 1 stands for in M/M/1), and parts or clients arriving at the server with an exponentially distributed inter-arrival time (the first M denotes that) and an exponentially distributed service time (the second M denotes that). 
For an M/M/1 system to be stable, the average service time, denoted as $\lambda$ should be less than the average inter-arrival time, denoted as $\mu$. The ratio between $\lambda$ and $\mu$ is called $\rho = \lambda / \mu$, and it is also known as the average utilization of the queueing system. 

For a typical queueing system, we are interested in simulating different arrival rates with different service times, and studying the following main output variables:

- queue length (development over time, average, standard deviation, min, max)
- time in queue (development over time, average, standard deviation, min, max)
- time in system (development over time, average, standard deviation, min, max)
- utilization of the server (development over time, average)

If we build a simulation model for this, we the following types of components:

1. **An entity** that flows through the model
2. **An arrival generator** that makes entities arrive with an interarrival time drawn from the correct distribution
3. **A server** that can serve entities for a given time
4. **A queue** in which waiting entities can be stored
5. **Output statistics objects** to gather the required 
6. **Random distributions** to draw the inter-arrival time and service time from
7. **A program** that can be started and that creates the simulator, model, and makes it work.


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

The first three statements are analogous to those in the `process()` method, but instead of decreasing the used capacity, it increases the used capacity by one. Statement 4 checks whether there are elements in the queue. If yes, we remove the first entry from the queue. the statistic for the queue length and the time-in-queue are updated, ather which the removed entity is offered to the `startProcess` method. The last stetement tallies the time-in-system of the entity. Since no statement using the entity comes afterward, the entity is removed from the model.

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
  mean = \sum{i=1}^N{\frac{v_i}{N}}
$$

where $v_i$ are the registered values, and $N$ is the number of registered values. If we register the values 2 and 4, the average is 3.

A persistent statistic is a time-weiged statistic that takes into account how long a certain value persisted. If we offer the value 2 for 10 time units, and the value 4 for 2 time units, the average is (10 * 2 + 2 * 4) / 12 = 2.33. Instead of dividing by the number of observations, we divide over the total time. The mean is calculated by:

$$
  mean = \int{0}^T{\frac{t_i * v_i}{T}} where T = \sum{i=1}^T{t_i}
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


### 6. Random distributions to draw the inter-arrival time and service time from
