package nl.tudelft.simulation.dsol;

/**
 * This class defines SimRuntimeException. This exception is thrown throughout DSOL whenever exceptions occur which are directly
 * linked to the simulator.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author Peter Jacobs, Alexander Verbraeck
 */
public class SimRuntimeException extends RuntimeException
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for SimRuntimeException.
     */
    public SimRuntimeException()
    {
        super();
    }

    /**
     * constructs a new SimRuntimeException.
     * @param message the exception message
     */
    public SimRuntimeException(final String message)
    {
        super(message);
    }

    /**
     * constructs a new SimRuntimeException.
     * @param message the exception message
     * @param cause the originating throwable
     */
    public SimRuntimeException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor for SimRuntimeException.
     * @param cause the originating throwable
     */
    public SimRuntimeException(final Throwable cause)
    {
        super(cause);
    }
}
