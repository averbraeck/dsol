package nl.tudelft.simulation.dsol.swing.gui.appearance;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Button with appearance control.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class AppearanceControlButton extends JButton implements AppearanceControl
{
    /** */
    private static final long serialVersionUID = 20180207L;

    /**
     * @param loadIcon Icon; icon
     */
    public AppearanceControlButton(final Icon loadIcon)
    {
        super(loadIcon);
    }

    @Override
    public boolean isFont()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "AppearanceControlButton []";
    }
}
