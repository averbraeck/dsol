package nl.tudelft.simulation.dsol.swing.gui.appearance;

import javax.swing.JLabel;

/**
 * Label with appearance control.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class AppearanceControlLabel extends JLabel implements AppearanceControl
{
    /** */
    private static final long serialVersionUID = 20180207L;

    /**
     * Generate an empty label with appearance control.
     */
    public AppearanceControlLabel()
    {
        super();
    }

    /**
     * Generate a label with appearance control with text.
     * @param text the text of the label
     */
    public AppearanceControlLabel(final String text)
    {
        super(text);
    }

    @Override
    public boolean isForeground()
    {
        return true;
    }

    @Override
    public boolean isBackground()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "AppearanceControlLabel []";
    }
}
