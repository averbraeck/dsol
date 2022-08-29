# Custom Time Types

## Custom simulation time type

In addition to the five provided simulation time types, it is possible to define an own type, and provide an implementation for the above classes that are characterized by the time type. A simulation time class `SimTime` is defined as follows:

```java
public abstract class SimTime<A extends Comparable<A>, 
    R extends Number & Comparable<R>, T extends SimTime<A, R, T>> 
    implements Serializable, Comparable<T>
```

The three generics A, R, and T have the following meaning:

* the absolute time type A, e.g. Time or Calendar for the absolute time. This is an object representing _absolute_ time, e.g., a Calendar for a simulation time. For simple simulation time types such as a double for simulation time, the relative and absolute storage types are the same. Absolute times should be `Comparable`, which means that we should be able to order times sequentially.
 * the relative duration type R, e.g. Duration or Double for the relative time. This is an object representing _relative_ time, so in case of a Calendar for absolute time, the relative storage type could be. e.g., Duration or Double. Relative times should be `Comparable`, and extend from `Number`, which means that we can retrieve a `doubleValue()` of the relative time.
 * the extended type T itself to be able to implement a comparator, and to ease the use of return types.

Suppose you have an absolute time class called `Tick`, and a relative time as `Long` values. The Tick class is implemented as follows:

```java
public class Tick implements Comparable<Tick>
{
    /** the tick value of the absolute time. */
    private long absoluteTick = 0;
    
    /**
     * Create a Tick time with the indicate absolute number of ticks.
     * @param absoluteTick the stating tick time
     */
    public Tick(final long absoluteTick)
    {
        this.absoluteTick = absoluteTick;
    }

    /**
     * Add a number of ticks to the current time. 
     * @param delta the number of ticks to add
     */
    public void add(final long delta)
    {
        this.absoluteTick += delta;
    }
    
    /**
     * Subtract a number of ticks from the current time. 
     * @param delta the number of ticks to subtract
     */
    public void subtract(final long delta)
    {
        this.absoluteTick -= delta;
    }
    
    /**
     * @return the absolute tick time as a long
     */
    public long getTickTime()
    {
        return this.absoluteTick;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final Tick tick)
    {
        return Long.compare(this.absoluteTick, tick.absoluteTick);
    }
}
```

In that case you could implement your simulation time `SimTimeTick` as follows:

```java
public class SimTimeTick extends SimTime<Tick, Long, SimTimeTick>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the stored absolute time. */
    private Tick tick;
    
    /**
     * @param time the initial tick time for SimTickTime
     */
    public SimTimeTick(final Tick time)
    {
        super(time);
    }

    /** {@inheritDoc} */
    @Override
    public void add(final Long relativeTime)
    {
        this.tick.add(relativeTime);
    }

    /** {@inheritDoc} */
    @Override
    public void subtract(final Long relativeTime)
    {
        this.tick.subtract(relativeTime);
    }

    /** {@inheritDoc} */
    @Override
    public SimTimeTick setZero()
    {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public SimTimeTick copy()
    {
        return new SimTimeTick(this.tick);
    }

    /** {@inheritDoc} */
    @Override
    public void set(final Tick absoluteTime)
    {
        this.tick = absoluteTime;
    }

    /** {@inheritDoc} */
    @Override
    public Tick get()
    {
        return this.tick;
    }

    /** {@inheritDoc} */
    @Override
    public Long diff(final Tick absoluteTime)
    {
        return this.tick.getTickTime() - absoluteTime.getTickTime();
    }

    /** {@inheritDoc} */
    @Override
    public Tick getAbsoluteZero()
    {
        return new Tick(0L);
    }

    /** {@inheritDoc} */
    @Override
    public Long getRelativeZero()
    {
        return 0L;
    }
}
```

To implement, e.g., a `DEVSSimulatorTick` based on the above time, extend the `DEVSSimulator` as follows:

```java
public class DEVSSimulatorTick extends DEVSSimulator<Tick, Long, SimTimeTick>
    implements DEVSSimulatorInterface<Tick, Long, SimTimeTick>
{
    /** */
    private static final long serialVersionUID = 1L;
}
```

You are now able to use this simulator in your model. 

```java
public class ModelTick
{
    /**
     * Create a runnable simulation program.
     * @param args empty
     */
    public static void main(String[] args)
    {
        DEVSSimulatorTick simulator = new DEVSSimulatorTick();
        Model model = new Model(simulator);
        // TODO: create an experiment and execute the model
    }

    /** The model class. */
    static class Model extends AbstractDSOLModel<Tick, Long, SimTimeTick, DEVSSimulatorTick>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Create the model class.
         * @param simulator the simulator to use.
         */
        public Model(final DEVSSimulatorTick simulator)
        {
            super(simulator);
        }

        /** {@inheritDoc} */
        @Override
        public void constructModel() throws SimRuntimeException
        {
            // Schedule an event at tick 10.
            SimEventInterface<SimTimeTick> tick10 =
                    new SimEvent<>(new SimTimeTick(new Tick(10L)), "this", "this", "report10", null);
            getSimulator().scheduleEvent(tick10);
        }
        
        /** the scheduled method. */
        protected void report10()
        {
            System.out.println("Called at time tick 10");
        }
    }
}
```
