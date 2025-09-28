package nl.tudelft.simulation.dsol.logger;

import org.djutils.logger.LogCategory;

/**
 * Predefined categories for Category logging in DSOL. <br>
 * <p>
 * Copyright (c) 2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See for
 * project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public final class Cat
{
    /** */
    private Cat()
    {
        // Utility class
    }

    /** DSOL project. Category is used for dsol-base, dsol-core, dsol-demo, dsol-interpreter, dsol-introspection. */
    public static final LogCategory DSOL = new LogCategory("DSOL");
    
}
