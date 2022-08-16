package nl.tudelft.simulation.jstats.streams;

/**
 * Exception for the Random Number Generators.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class StreamException extends Exception
{
    /** */
    private static final long serialVersionUID = 1L;

    /** */
    public StreamException()
    {
        super();
    }

    /**
     * @param message String; the description of the exception.
     */
    public StreamException(final String message)
    {
        super(message);
    }

    /**
     * @param cause Throwable; the earlier exception on which this exception is based.
     */
    public StreamException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message String; the description of the exception.
     * @param cause Throwable; the earlier exception on which this exception is based.
     */
    public StreamException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param message String; the description of the exception.
     * @param cause Throwable; the earlier exception on which this exception is based.
     * @param enableSuppression boolean; ..
     * @param writableStackTrace boolean; ..
     */
    public StreamException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
