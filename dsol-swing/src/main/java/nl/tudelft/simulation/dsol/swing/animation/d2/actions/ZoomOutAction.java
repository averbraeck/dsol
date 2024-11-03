package nl.tudelft.simulation.dsol.swing.animation.d2.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.djutils.io.URLResource;

import nl.tudelft.simulation.dsol.swing.animation.d2.VisualizationPanel;

/**
 * The ZoomOut action.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="mailto:nlang@fbk.eur.nl">Niels Lang </a>, <a href="http://www.peter-jacobs.com">Peter Jacobs </a>
 */
public class ZoomOutAction extends AbstractAction
{
    /** */
    private static final long serialVersionUID = 20140909L;

    /** the panel to zoom out. */
    private VisualizationPanel panel = null;

    /**
     * constructs a new ZoomOutAction.
     * @param panel GridPanel; the target
     */
    public ZoomOutAction(final VisualizationPanel panel)
    {
        super("ZoomOut");
        this.panel = panel;
        this.putValue(Action.SMALL_ICON,
                new ImageIcon(URLResource.getResource("/toolbarButtonGraphics/general/ZoomOut16.gif")));
        this.setEnabled(true);
    }

    /**
     * @see java.awt.event.ActionListener #actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        this.panel.zoom(VisualizationPanel.ZOOMFACTOR);
        this.panel.requestFocus();
    }
}
