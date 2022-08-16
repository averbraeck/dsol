package nl.tudelft.simulation.dsol.swing.gui.animation.panel;

import java.rmi.RemoteException;

import javax.swing.JPanel;

import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;

/**
 * The PropertiesPanel displays the properties of one or more selected objects.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PropertiesPanel extends JPanel implements EventListenerInterface
{
    /** */
    private static final long serialVersionUID = 20210214L;

    /**
     * Create a properties panel to display properties of one or more selected objects. The actual creation of the panel is left
     * to the init method.
     */
    public PropertiesPanel()
    {
        init();
    }

    /**
     * Create the properties panel to be ready to display the properties of one or more selected objects.
     */
    protected void init()
    {
        // nothing yet
    }
    
    /** {@inheritDoc} */
    @Override
    public void notify(final EventInterface event) throws RemoteException
    {
        // nothing yet
    }

}
