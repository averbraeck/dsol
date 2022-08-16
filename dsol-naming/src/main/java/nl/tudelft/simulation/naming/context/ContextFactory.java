package nl.tudelft.simulation.naming.context;

import java.util.Hashtable;

import javax.naming.NamingException;

/**
 * This interface represents a factory that creates an initial context. It is based on the InitialContentFactory from
 * javax.naming but creates a lightweight ContextInterface rather than the heavyweight JNDI context.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface ContextFactory
{
    /**
     * Creates an Initial Context for beginning name resolution. Special requirements of this context are supplied using
     * <code>environment</code>. Different implementations exist, such as the JVMContext, the FileContext and the RemoteContext.
     * A remote context registers itself in the RMI registry using a provided key from the environment. The remote event
     * producer for the remote context uses that same key, extended with the string "_producer".
     * @param environment The possibly null environment specifying information to be used in the creation of the initial context
     * @param atomicName String; the name under which the root context will be registered
     * @return A non-null initial context object that implements the ContextInterface
     * @throws NamingException when the initial context could not be created
     */
    ContextInterface getInitialContext(Hashtable<?, ?> environment, final String atomicName) throws NamingException;
}
