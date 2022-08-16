package nl.tudelft.simulation.dsol.interpreter.process;

import java.util.Stack;

import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.logger.CategoryLogger;
import org.djutils.reflection.ClassUtil;

import nl.tudelft.simulation.dsol.interpreter.Frame;
import nl.tudelft.simulation.dsol.interpreter.Interpreter;
import nl.tudelft.simulation.dsol.interpreter.InterpreterException;

/**
 * The Process class is an abstract Process which can be suspended and resumed.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class InterpretableProcess extends EventProducer
{
    /** */
    private static final long serialVersionUID = 20140830L;

    /** the initial state. */
    public static final short INITIAL = 0;

    /** the initial state. */
    public static final short EXECUTING = 1;

    /** the initial state. */
    public static final short SUSPENDED = 2;

    /** the initial state. */
    public static final short DEAD = 3;

    /** the EventType. */
    public static final EventType STATE_CHANGE_EVENT = new EventType("STATE_CHANGE_EVENT");

    /** the state of the process. */
    private short state = InterpretableProcess.INITIAL;

    /** the processStack of this process. */
    private final Stack<Frame> frameStack = new Stack<Frame>();

    /**
     * constructs a new Process.
     */
    public InterpretableProcess()
    {
        super();
        try
        {
            this.frameStack.push(Interpreter.createFrame(this, ClassUtil.resolveMethod(this, "process", null), null));
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception, "<init>");
        }
    }

    /**
     * resumes this process.
     */
    public void resumeProcess()
    {
        if (this.frameStack.isEmpty() || this.state == DEAD)
        {
            this.frameStack.clear();
            return;
        }
        if (this.state == EXECUTING)
        {
            throw new IllegalStateException("Cannot resume a process in state==executing");
        }
        try
        {
            this.setState(InterpretableProcess.EXECUTING);
            this.frameStack.peek().setPaused(false);
            Interpreter.interpret(this.frameStack);
        }
        catch (InterpreterException exception)
        {
            CategoryLogger.always().error(exception);
        }
    }

    /**
     * cancels this process entirely. After the process.cancelProcess() is invoked a process can no longer be resumed.
     */
    public void cancelProcess()
    {
        boolean executing = this.state == EXECUTING;
        if (executing)
        {
            this.suspendProcess();
        }
        this.state = InterpretableProcess.DEAD;
        if (executing)
        {
            this.resumeProcess();
        }
    }

    /**
     * suspends a process.
     */
    public void suspendProcess()
    {
        throw new IllegalStateException(
                "suspend should be interpreted." + " One may not invoke this method directly. If this exception occurs, "
                        + "make sure that the method that invoked it, was interpreted.");
    }

    /**
     * sets the state of the process.
     * @param state short; the new state
     */
    public void setState(final short state)
    {
        // Let's check for a reliable order
        if (this.state == InterpretableProcess.SUSPENDED && state == InterpretableProcess.SUSPENDED)
        {
            throw new IllegalStateException("Cannot suspend a suspended process");
        }
        this.state = state;
        super.fireEvent(STATE_CHANGE_EVENT, state);
    }

    /**
     * Returns the state of a process.
     * @return the state
     */
    public short getState()
    {
        return this.state;
    }
}
