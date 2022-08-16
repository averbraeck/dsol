package nl.tudelft.simulation.dsol.swing.gui;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * JPanel with an outline and a name. Code based on OpenTrafficSim project component with the same purpose.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class LabeledPanel extends JPanel
{
    /** */
    private static final long serialVersionUID = 20141222L;

    /**
     * Create a JPanel with border and caption.
     * @param caption String; the caption of the LabeledPanel
     */
    public LabeledPanel(final String caption)
    {
        setBorder(new TitledBorder(null, caption, TitledBorder.LEADING, TitledBorder.TOP, null, null));
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "LabeledPanel [caption=" + ((TitledBorder) getBorder()).getTitle() + "]";
    }
}
