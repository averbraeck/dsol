package nl.tudelft.simulation.dsol.swing.animation.D2.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.djutils.io.URLResource;

import nl.tudelft.simulation.dsol.swing.animation.D2.GridPanel;

/**
 * The Nome action, restoring the viewport to the original extent.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 */
public class HomeAction extends AbstractAction
{
    /** */
    private static final long serialVersionUID = 20140909L;

    /** target of the gridpanel. */
    private GridPanel panel = null;

    /**
     * constructs a new ZoomIn.
     * @param panel GridPanel; the target
     */
    public HomeAction(final GridPanel panel)
    {
        super("Home");
        this.panel = panel;
        this.putValue(Action.SMALL_ICON,
                new ImageIcon(URLResource.getResource("/toolbarButtonGraphics/navigation/Home16.gif")));
        this.setEnabled(true);
    }

    /**
     * @see java.awt.event.ActionListener #actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        this.panel.home();
        this.panel.requestFocus();
    }
}
