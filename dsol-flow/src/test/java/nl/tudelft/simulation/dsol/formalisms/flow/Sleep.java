package nl.tudelft.simulation.dsol.formalisms.flow;

/**
 * Sleep implements a sleep without an InterruptedException.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public final class Sleep
{
    /**
     * Utility class.
     */
    private Sleep()
    {
        // Utility class
    }

    /**
     * Sleep millis milliseconds.
     * @param millis the number of milliseconds to sleep
     */
    public static void sleep(final long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException exception)
        {
            // ignore
        }
    }

    /**
     * Sleep millis + nanos time.
     * @param millis the number of milliseconds to sleep
     * @param nanos the number of nanoseconds to sleep
     */
    public static void sleep(final long millis, final int nanos)
    {
        try
        {
            Thread.sleep(millis, nanos);
        }
        catch (InterruptedException exception)
        {
            // ignore
        }
    }

}
