package nl.tudelft.simulation.dsol.swing;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import nl.tudelft.simulation.dsol.swing.gui.util.Icons;

/**
 * ButtonUtil is a helper class to create buttons for the DSOL GUI.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class ButtonUtil
{
    /** Utility class. */
    private ButtonUtil()
    {
        // Utility class
    }

    /**
     * Create a button.
     * @param actionListener ActionListener; the listener to the press of the button
     * @param name String; name of the button
     * @param iconPath String; path to the resource
     * @param actionCommand String; the action command
     * @param toolTipText String; the hint to show when the mouse hovers over the button
     * @param enabled boolean; true if the new button must initially be enable; false if it must initially be disabled
     * @return JButton
     */
    public static JButton makeButton(final ActionListener actionListener, final String name, final String iconPath,
            final String actionCommand, final String toolTipText, final boolean enabled)
    {
        JButton result = new JButton(Icons.loadIcon(iconPath));
        result.setPreferredSize(new Dimension(34, 32));
        result.setName(name);
        result.setEnabled(enabled);
        result.setActionCommand(actionCommand);
        result.setToolTipText(toolTipText);
        result.addActionListener(actionListener);
        return result;
    }

}
