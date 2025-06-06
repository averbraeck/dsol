package nl.tudelft.simulation.dsol.formalisms.eventscheduling;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.djutils.exceptions.Throw;
import org.djutils.reflection.ClassUtil;

import nl.tudelft.simulation.dsol.SimRuntimeException;

/**
 * The SimEvent forms the essential scheduling mechanism for D-SOL. Objects do not invoke methods directly on eachother; they
 * bundle the object on which the method is planned to be invoked together with the arguments and the name of the method in a
 * simEvent. The SimEvent is then stored in the eventList and executed.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the type of simulation time, e.g. Double, Long or Duration.
 * @since 1.5
 */
public class SimEvent<T extends Number & Comparable<T>> extends AbstractSimEvent<T>
{
    /** */
    private static final long serialVersionUID = 20140804L;

    /** target the target on which a state change is scheduled. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Object target = null;

    /** method is the method which embodies the state change. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected String methodName = null;

    /** args are the arguments that are used to invoke the method with. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Object[] args = null;

    /**
     * The constructor of the event stores the time the event must be executed and the object and method to invoke.
     * @param executionTime the absolute time the event has to be executed.
     * @param target the object on which the method must be invoked.
     * @param method the method to invoke
     * @param args the arguments the method to invoke with
     */
    public SimEvent(final T executionTime, final Object target, final String method, final Object[] args)
    {
        this(executionTime, SimEvent.NORMAL_PRIORITY, target, method, args);
    }

    /**
     * The constructor of the event stores the time the event must be executed and the object and method to invoke.
     * @param executionTime the time the event has to be executed.
     * @param priority the priority of the event
     * @param target the object on which the method must be invoked.
     * @param method the method to invoke
     * @param args the arguments the method to invoke with
     */
    public SimEvent(final T executionTime, final short priority, final Object target, final String method, final Object[] args)
    {
        super(executionTime, priority);
        Throw.whenNull(target, "SimEvent target is null");
        Throw.whenNull(method, "SimEvent method is null");
        this.target = target;
        this.methodName = method;
        this.args = args;
    }

    @Override
    public synchronized void execute() throws SimRuntimeException
    {
        try
        {
            if (this.methodName.equals("<init>"))
            {
                if (!(this.target instanceof Class))
                { throw new SimRuntimeException("Invoking a constructor implies that target should be instance of Class"); }
                Constructor<?> constructor = ClassUtil.resolveConstructor((Class<?>) this.target, this.args);
                constructor.setAccessible(true);
                constructor.newInstance(this.args);
            }
            else
            {
                Method method = ClassUtil.resolveMethod(this.target, this.methodName, this.args);
                method.setAccessible(true);
                method.invoke(this.target, this.args);
            }
        }
        catch (Exception exception)
        {
            System.err.println(exception.toString() + " calling " + printTarget() + "." + this.methodName + " with arguments "
                    + printArgs());
            throw new SimRuntimeException(exception);
        }
    }

    /**
     * @return Returns the args.
     */
    public Object[] getArgs()
    {
        return this.args;
    }

    /**
     * @return Returns the method.
     */
    public String getMethod()
    {
        return this.methodName;
    }

    /**
     * @return Returns the target.
     */
    public Object getTarget()
    {
        return this.target;
    }

    /**
     * Retrieve the target in a human readable way.
     * @return the target in a human readable way
     */
    public String printTarget()
    {
        String s = "{" + this.target.getClass().getSimpleName() + ":";
        try
        {
            s += this.target.toString() + "}";
        }
        catch (Exception e)
        {
            s += "Error in toString()}";
        }
        return s;
    }

    /**
     * Retrieve the arguments in a human readable way.
     * @return the arguments in a human readable way
     */
    public String printArgs()
    {
        if (getArgs() == null)
        { return "null"; }
        List<String> argsList = new ArrayList<>();
        for (int i = 0; i < this.getArgs().length; i++)
        {
            String s = "{" + this.getArgs()[i].getClass().getSimpleName() + ":";
            try
            {
                s += this.getArgs()[i].toString() + "}";
            }
            catch (Exception e)
            {
                s += "Error in toString()}";
            }
            argsList.add(s);
        }
        return argsList.toString();
    }

    @Override
    public String toString()
    {
        return "SimEvent[time=" + this.absoluteExecutionTime + "; priority=" + this.priority + "; target=" + printTarget()
                + "; method=" + this.methodName + "; args=" + printArgs() + "]";
    }

}
