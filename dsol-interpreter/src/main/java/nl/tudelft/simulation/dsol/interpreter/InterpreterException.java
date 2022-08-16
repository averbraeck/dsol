package nl.tudelft.simulation.dsol.interpreter;

/**
 * An InterpreterException.
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
public class InterpreterException extends RuntimeException
{
    /** */
    private static final long serialVersionUID = 20140830L;

    /**
     * constructs a new InterpreterException.
     */
    public InterpreterException()
    {
        super();
    }

    /**
     * constructs a new InterpreterException.
     * @param message String; the message
     */
    public InterpreterException(final String message)
    {
        super(message);
    }

    /**
     * constructs a new InterpreterException.
     * @param message String; the message
     * @param cause Throwable; the cause
     */
    public InterpreterException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * constructs a new InterpreterException.
     * @param cause Throwable; the cause
     */
    public InterpreterException(final Throwable cause)
    {
        super(cause);
    }
}
