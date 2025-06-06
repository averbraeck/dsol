package nl.tudelft.simulation.dsol.formalisms.eventscheduling;

import nl.tudelft.simulation.dsol.SimRuntimeException;

/**
 * The SimEvent forms the essential scheduling mechanism for D-SOL. Objects do not invoke methods directly on eachother; they
 * bundle the object on which the method is planned to be invoked together with the arguments and the name of the method in a
 * simEvent. The SimEvent is then stored in the eventList and executed.
 * <p>
 * Copyright (c) 2016-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the type of simulation time, e.g. Double, Long or Duration
 * @since 1.5
 */
public class LambdaSimEvent<T extends Number & Comparable<T>> extends AbstractSimEvent<T>
{
    /** */
    private static final long serialVersionUID = 20140804L;

    /** executable is the lambda expression tghat takes care of the state change. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Executable executable = null;

    /**
     * The constructor of the event stores the time the event must be executed and the object and method to invoke.
     * @param executionTime the absolute time the event has to be executed.
     * @param executable the lambda method to invoke
     */
    public LambdaSimEvent(final T executionTime, final Executable executable)
    {
        this(executionTime, SimEvent.NORMAL_PRIORITY, executable);
    }

    /**
     * The constructor of the event stores the time the event must be executed and the object and method to invoke.
     * @param executionTime the time the event has to be executed.
     * @param priority the priority of the event
     * @param executable the lambda method to invoke
     */
    public LambdaSimEvent(final T executionTime, final short priority, final Executable executable)
    {
        super(executionTime, priority);
        if (executable == null)
        { throw new IllegalArgumentException("executable==null"); }
        this.executable = executable;
    }

    @Override
    public synchronized void execute() throws SimRuntimeException
    {
        try
        {
            this.executable.execute();
        }
        catch (Exception exception)
        {
            throw new SimRuntimeException(exception);
        }
    }

    @Override
    public String toString()
    {
        return "SimEvent[time=" + this.absoluteExecutionTime + "; priority=" + this.priority + "; executable=" + this.executable
                + "]";
    }

}
