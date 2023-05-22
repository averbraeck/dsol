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
For the generation of the entities, we create a method called `generate()`, that we call when constructing the model. The `generate` method creates one entity and either offers it to the `process(Entity)` method when the server is idle, or adds it to the rear of the queue when the server is busy. 

After the just created entity has been handled, the `generate()` method calls itself after a time equal to a random value drawn from the Exponential inter-arrival time distribution. In a sense, the method re-schedules itself indefinitely, each time with an inter-arrival time from a given exponential distribution with $\lambda$ as the parameter.

The code to generate an entity would be:

```java
    protected void generate() throws SimRuntimeException
    {
        Entity entity = new Entity(this.entityCounter++, 
            this.simulator.getSimulatorTime());
        synchronized (this.queue)
        {
            if (this.capacity - this.busy >= 1)
            {
                // process
                startProcess(entity);
            }
            else
            {
                // queue
                this.persistentQueueLength.register(getSimulator().getSimulatorTime(),
                    this.queue.size());
                this.queue.add(new QueueEntry<Entity>(entity, 
                    this.simulator.getSimulatorTime()));
            }
        }
        this.simulator.scheduleEventRel(this.interarrivalTime.draw(), 
            this, "generate", null);
    }
```

A few explanations:

- The `Model` class has a queue called `queue` (see #4), a server capacity called `capacity` (default 1), and a number of entities being processed by the server at this moment called `busy` (0 or 1 in the default situation).
- The method `simulator.getSimulatorTime()` that is used a few times returns the current time of the simulator.
- There is a statistic called `persistentQueueLength` (see #5) that keeps the statistics for the queue length. Every time an entity enters the queue or leaves the queue, the statistic is updated.
- The last line re-schedules the `generate()` method. It indicates that we should schedule after a relative duration (`scheduleEventRel`) and not at an absolute point in time. The interarrival time is drawn from a distribution object (see #6) called `interarrivalTime`. The last three arguments indicate the object instance on which the method will eventually be scheduled (`this`), the name of the method (`generate`) and the arguments to pass to the method (`null`), so no arguments.

!!! Note
    The time stays constant as long as the executed event (method) is busy. If the execution of a method takes 5 minutes on the wall clock (real time), the simulation clock is standing still for that entire duration. On the other hand, if the next event is 1000 years later than the last event, the simulator clock will instantaneously jump 1000 years ahead to execute the next event.

!!! Note
    The execution of the methods is single-threaded. No parallel execution occurs, and the `Simulator` carries out one event at a time, so we do not have to be afraid that another entity is being generated while we did not yet increase the `busy` flag of the server. The next event will only be carried out when the current event, including method calls to other methods, has been completed. 


### 3. A server with an attached queue
When the server is free, it is offered a generated entity at some time. To indicate that the server is busy, we set the 