package nl.tudelft.simulation.dsol.swing.gui.animation.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.swing.ButtonUtil;
import nl.tudelft.simulation.dsol.swing.animation.D2.AnimationPanel;

/**
 * The ButtonPanel with the home button, grid button, and full extent button are synchronously coupled to the animation panel.
 * There are no other interpretations than making direct changes in the animation panel. The functions to show/hide the grid and
 * to change the extent are basic functions of the AnimationPanel and can be expected to always be present.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ButtonPanel extends JPanel implements ActionListener
{
    /** */
    private static final long serialVersionUID = 20210214L;

    /** the animation panel on which the buttons work. */
    private final AnimationPanel animationPanel;

    /**
     * Create a button panel; delegate the construction to the init() method, which can be overridden.
     * @param animationPanel AnimationPanel; the animation panel on which the buttons work
     */
    public ButtonPanel(final AnimationPanel animationPanel)
    {
        this.animationPanel = animationPanel;
        init();
    }

    /**
     * Construct the panel; this method can be overridden for another look and feel, or for adding buttons.
     */
    public void init()
    {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(ButtonUtil.makeButton(this, "allButton", "/resources/Expand.png", "ZoomAll", "Zoom whole network", true));
        add(ButtonUtil.makeButton(this, "homeButton", "/resources/Home.png", "Home", "Zoom to original extent", true));
        add(ButtonUtil.makeButton(this, "gridButton", "/resources/Grid.png", "Grid", "Toggle grid on/off", true));
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        String actionCommand = actionEvent.getActionCommand();
        try
        {
            if (actionCommand.equals("Home"))
            {
                this.animationPanel.home();
            }
            if (actionCommand.equals("ZoomAll"))
            {
                this.animationPanel.zoomAll();
            }
            if (actionCommand.equals("Grid"))
            {
                this.animationPanel.showGrid(!this.animationPanel.isShowGrid());
            }
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception);
        }
    }
}
