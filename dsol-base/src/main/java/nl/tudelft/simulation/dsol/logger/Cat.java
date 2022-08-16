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
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
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

    /** DSOL-NAMING project. */
    public static final LogCategory NAMING = new LogCategory("NAMING");

    /** DSOL-EVENT project. */
    public static final LogCategory EVENT = new LogCategory("EVENT");

    /** DSOL-SWING project. */
    public static final LogCategory SWING = new LogCategory("SWING");

    /** DSOL-WEB project. */
    public static final LogCategory WEB = new LogCategory("WEB");

    /** DSOL-HLA project. */
    public static final LogCategory HLA = new LogCategory("HLA");

}
