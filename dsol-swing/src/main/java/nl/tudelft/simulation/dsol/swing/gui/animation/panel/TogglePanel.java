package nl.tudelft.simulation.dsol.swing.gui.animation.panel;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationTab;

/**
 * TogglePanel.java.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TogglePanel extends JPanel
{
    /** */
    private static final long serialVersionUID = 20210214L;

    /**
     * Create a TogglePanel to switch objects / layers in the animation on or off.
     * @param animationTab the tab with the methods to switch objects / layers on or off
     */
    public TogglePanel(final DsolAnimationTab animationTab)
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

}
