package nl.tudelft.simulation.dsol.animation.gis;

/**
 * DsolGisException for exceptions when reading or drawing GIS layers.
 * <p>
 * Copyright (c) 2020- 2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DsolGisException extends Exception
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new blank DsolGisException.
     */
    public DsolGisException()
    {
        super();
    }

    /**
     * Constructs a new DsolGisException.
     * @param message String; the message to display
     * @param cause Throwable; the underlying exception
     */
    public DsolGisException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new DsolGisException.
     * @param message String; the message to display
     */
    public DsolGisException(final String message)
    {
        super(message);
    }

    /**
     * Constructs a new DsolGisException.
     * @param cause Throwable; the underlying exception
     */
    public DsolGisException(final Throwable cause)
    {
        super(cause);
    }

}
