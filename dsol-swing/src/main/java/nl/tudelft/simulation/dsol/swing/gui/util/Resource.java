package nl.tudelft.simulation.dsol.swing.gui.util;

import java.io.InputStream;

/**
 * Resource utility. Code based on OpenTrafficSim project component with the same purpose.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public final class Resource
{

    /** Constructor. */
    private Resource()
    {
        //
    }

    /**
     * Obtains stream for resource, either in IDE or java.
     * @param name String; name of resource
     * @return the resolved input stream
     */
    public static InputStream getResourceAsStream(final String name)
    {
        InputStream stream = Resource.class.getResourceAsStream(name);
        if (stream != null)
        {
            return stream;
        }
        stream = Resource.class.getResourceAsStream("/resources" + name);
        if (stream != null)
        {
            return stream;
        }
        throw new RuntimeException("Unable to load resource " + name);
    }

}
