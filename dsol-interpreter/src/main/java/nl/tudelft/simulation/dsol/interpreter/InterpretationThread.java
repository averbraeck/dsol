package nl.tudelft.simulation.dsol.interpreter;

import org.djutils.logger.CategoryLogger;

/**
 * An InterpretationThread to interpret runnable code.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @since 1.5
 */
public class InterpretationThread extends Thread
{
    /** the target of this interpretation. */
    private Runnable target = null;

    /**
     * constructs a new InterpretationThread.
     * @param target Runnable; the target.
     */
    public InterpretationThread(final Runnable target)
    {
        super();
        this.target = target;
    }

    /**
     * constructs a new InterpretationThread.
     * @param target Runnable; the target.
     * @param name String; the name of the thread
     */
    public InterpretationThread(final Runnable target, final String name)
    {
        super(name);
        this.target = target;
    }

    /**
     * constructs a new InterpretationThread.
     * @param group ThreadGroup; the threadGroup
     * @param target Runnable; the target.
     */
    public InterpretationThread(final ThreadGroup group, final Runnable target)
    {
        super(group, target);
        this.target = target;
    }

    /**
     * constructs a new InterpretationThread.
     * @param group ThreadGroup; the threadGroup
     * @param target Runnable; the target.
     * @param name String; the name of the thread
     */
    public InterpretationThread(final ThreadGroup group, final Runnable target, final String name)
    {
        super(group, target, name);
        this.target = target;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        try
        {
            Interpreter.invoke(this.target, this.target.getClass().getDeclaredMethod("run"), null);
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception, "run");
        }
    }
}
