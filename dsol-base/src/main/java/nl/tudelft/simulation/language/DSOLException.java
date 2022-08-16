package nl.tudelft.simulation.language;

/**
 * DSOLException is used for generic exceptions in the DSOL packages.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class DSOLException extends Exception
{
    /** */
    private static final long serialVersionUID = 1L;

    /** */
    public DSOLException()
    {
        // empty constructor
    }

    /**
     * @param message String; the message of the exception
     */
    public DSOLException(final String message)
    {
        super(message);
    }

    /**
     * @param cause Throwable; the exception that caused this exception to be triggered
     */
    public DSOLException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message String; the message of the exception
     * @param cause Throwable; the exception that caused this exception to be triggered
     */
    public DSOLException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param message String; the message of the exception
     * @param cause Throwable; the exception that caused this exception to be triggered
     * @param enableSuppression boolean; whether or not suppression is enabled or disabled
     * @param writableStackTrace boolean; whether or not the stack trace should be writable
     */
    public DSOLException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
