package nl.tudelft.simulation.language;

/**
 * DsolRuntimeException is used for generic exceptions in the DSOL packages, where the exception is not specifically mentioned
 * in the method signature.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class DsolRuntimeException extends RuntimeException
{
    /** */
    private static final long serialVersionUID = 1L;

    /** */
    public DsolRuntimeException()
    {
        // empty constructor
    }

    /**
     * @param message the message of the exception
     */
    public DsolRuntimeException(final String message)
    {
        super(message);
    }

    /**
     * @param cause the exception that caused this exception to be triggered
     */
    public DsolRuntimeException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message the message of the exception
     * @param cause the exception that caused this exception to be triggered
     */
    public DsolRuntimeException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param message the message of the exception
     * @param cause the exception that caused this exception to be triggered
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public DsolRuntimeException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
