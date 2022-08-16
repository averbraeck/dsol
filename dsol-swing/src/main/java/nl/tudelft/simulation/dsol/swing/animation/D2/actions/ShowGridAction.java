package nl.tudelft.simulation.dsol.swing.animation.D2.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import nl.tudelft.simulation.dsol.swing.animation.D2.GridPanel;

/**
 * Show or hide the grid.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 */
public class ShowGridAction extends AbstractAction
{
    /** */
    private static final long serialVersionUID = 20140909L;

    /** the panel to show the grid on. */
    private GridPanel panel = null;

    /**
     * constructs a new ShowGridAction.
     * @param panel GridPanel; the target
     */
    public ShowGridAction(final GridPanel panel)
    {
        super("ShowGrid");
        this.panel = panel;
        this.setEnabled(true);
    }

    /**
     * @see java.awt.event.ActionListener #actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        this.panel.showGrid(!this.panel.isShowGrid());
        this.panel.requestFocus();
    }
}
