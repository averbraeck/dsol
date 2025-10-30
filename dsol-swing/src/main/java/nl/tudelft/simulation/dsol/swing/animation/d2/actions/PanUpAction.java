package nl.tudelft.simulation.dsol.swing.animation.d2.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.djutils.io.ResourceResolver;

import nl.tudelft.simulation.dsol.swing.animation.d2.VisualizationPanel;

/**
 * The PanUpAction.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="mailto:nlang@fbk.eur.nl">Niels Lang </a>, <a href="http://www.peter-jacobs.com">Peter Jacobs </a>
 */
public class PanUpAction extends AbstractAction
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the panel to pan up. */
    private VisualizationPanel panel = null;

    /**
     * constructs a new PanUp.
     * @param panel the target
     */
    public PanUpAction(final VisualizationPanel panel)
    {
        super("PanUp");
        this.panel = panel;
        this.putValue(Action.SMALL_ICON,
                new ImageIcon(ResourceResolver.resolve("/toolbarButtonGraphics/navigation/Up16.gif").asUrl()));
        this.setEnabled(true);
    }

    /**
     * @see java.awt.event.ActionListener #actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        this.panel.pan(VisualizationPanel.UP, 0.1);
        this.panel.requestFocus();
    }
}
