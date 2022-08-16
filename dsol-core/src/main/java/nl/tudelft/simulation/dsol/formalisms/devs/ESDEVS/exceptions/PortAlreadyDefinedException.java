package nl.tudelft.simulation.dsol.formalisms.devs.ESDEVS.exceptions;

/**
 * PortAlreadyDefinedException class. An input or output port with the given name has already been defined for the model.
 * <p>
 * Copyright (c) 2009-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a><br>
 */
public class PortAlreadyDefinedException extends Exception
{

    /** the default serial version UId. */
    private static final long serialVersionUID = 1L;

    /**
     * default constructor.
     */
    public PortAlreadyDefinedException()
    {
        //
    }

    /**
     * @param message String; the message to display / print for the exception
     */
    public PortAlreadyDefinedException(final String message)
    {
        super(message);
    }

    /**
     * @param cause Throwable; the Throwable or Exception that caused this exception to occur
     */
    public PortAlreadyDefinedException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message String; the message to display / print for the exception
     * @param cause Throwable; the Throwable or Exception that caused this exception to occur
     */
    public PortAlreadyDefinedException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
