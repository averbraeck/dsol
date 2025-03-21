package nl.tudelft.simulation.dsol.swing;

import java.awt.Container;
import java.rmi.RemoteException;

/**
 * An interface implemented by all charts and statistics objects defining their capability to present themselves as Swing
 * component. These components can be dropped on any GUI panel.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */

public interface Swingable
{
    /**
     * represents this statisticsObject as Swing Container.
     * @return a Container representation of an object
     * @throws RemoteException on network failure
     */
    Container getSwingPanel() throws RemoteException;
}
