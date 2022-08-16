package nl.tudelft.simulation.naming.context;

import java.util.Hashtable;

/**
 * A factory for JVMContext instances, automatically invoked by JNDI when the correct jndi.properties file has been used.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class JVMContextFactory implements ContextFactory
{
    /** context refers to the static JVMContext. */
    private static JVMContext context = null;

    /** {@inheritDoc} */
    @Override
    public synchronized ContextInterface getInitialContext(final Hashtable<?, ?> environment, final String atomicName)
    {
        if (context == null)
        {
            JVMContextFactory.context = new JVMContext(atomicName);
        }
        return context;
    }
}
