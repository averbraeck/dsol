package nl.tudelft.simulation.naming.context;

import java.util.Hashtable;

/**
 * A factory for JvmContext instances, automatically invoked by JNDI when the correct jndi.properties file has been used.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class JvmContextFactory implements ContextFactory
{
    /** context refers to the static JvmContext. */
    private static JvmContext context = null;

    @Override
    public synchronized ContextInterface getInitialContext(final Hashtable<?, ?> environment, final String atomicName)
    {
        if (context == null)
        {
            JvmContextFactory.context = new JvmContext(atomicName);
        }
        return context;
    }
}
