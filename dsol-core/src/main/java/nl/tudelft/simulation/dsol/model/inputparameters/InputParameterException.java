package nl.tudelft.simulation.dsol.model.inputparameters;

/**
 * Exception thrown when an operation is attempted that is not compatible with the indicated input parameter.
 * <p>
 * Copyright (c) 2013-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * <p>
 * $LastChangedDate: 2016-05-28 11:33:31 +0200 (Sat, 28 May 2016) $, @version $Revision: 2051 $, by $Author: averbraeck $,
 * initial version 18 dec. 2014 <br>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class InputParameterException extends Exception
{
    /**  */
    private static final long serialVersionUID = 20141023L;

    /**
     * Construct a new InputParameterException.
     */
    public InputParameterException()
    {
        super();
    }

    /**
     * Construct a new InputParameterException.
     * @param message String; description of the problem
     */
    public InputParameterException(final String message)
    {
        super(message);
    }

    /**
     * Construct a new InputParameterException.
     * @param cause Throwable; the cause of this ValueException
     */
    public InputParameterException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Construct a new InputParameterException.
     * @param message String; description of the problem
     * @param cause Throwable; the cause of this ValueException
     */
    public InputParameterException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Construct a new InputParameterException.
     * @param message String; description of the problem
     * @param cause Throwable; the cause of this ValueException
     * @param enableSuppression boolean; whether or not suppression is enabled or disabled
     * @param writableStackTrace boolean; whether or not the stack trace should be writable
     */
    public InputParameterException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
