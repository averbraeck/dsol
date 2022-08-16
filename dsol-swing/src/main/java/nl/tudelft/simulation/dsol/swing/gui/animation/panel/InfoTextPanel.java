package nl.tudelft.simulation.dsol.swing.gui.animation.panel;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import nl.tudelft.simulation.dsol.swing.animation.D2.AnimationPanel;

/**
 * A panel with info in the north bar of the animation tab. This version show the mouse position.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InfoTextPanel extends JPanel
{
    /** */
    private static final long serialVersionUID = 20210214L;

    /** the animation panel that has the latest mouse coordinate. */
    private final AnimationPanel animationPanel;

    /** The coordinates of the cursor. */
    private JLabel coordinateField;

    /** The default formatter for the world coordinates. */
    private static final NumberFormat COORDINATE_FORMATTER = NumberFormat.getInstance();

    /** The formatter instance for the world coordinates. */
    private NumberFormat coordinateFormatter = NumberFormat.getInstance();

    /** Initialize the default coordinate formatter. */
    static
    {
        COORDINATE_FORMATTER.setMaximumFractionDigits(3);
    }

    /**
     * Create a panel with info in the north bar of the animation tab. This version show the mouse position. The actual creation
     * is left to an overridable init method.
     * @param animationPanel AnimationPanel; the animation panel that has the latest mouse coordinate
     */
    public InfoTextPanel(final AnimationPanel animationPanel)
    {
        this.animationPanel = animationPanel;
        this.coordinateFormatter = COORDINATE_FORMATTER;
        init();
    }

    /**
     * Create a panel with the mouse position.
     */
    public void init()
    {
        setMinimumSize(new Dimension(250, 35));
        setPreferredSize(new Dimension(250, 35));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.coordinateField = new JLabel("Mouse: ");
        this.coordinateField.setMinimumSize(new Dimension(250, 10));
        this.coordinateField.setPreferredSize(new Dimension(250, 10));
        add(this.coordinateField);

        // run the update task for the mouse coordinate panel
        new UpdateTimer().start();
    }

    /**
     * Display the latest world coordinate based on the mouse position on the screen.
     */
    public void updateWorldCoordinate()
    {
        String worldPoint = "(x=" + this.coordinateFormatter.format(this.animationPanel.getWorldCoordinate().getX()) + " ; y="
                + this.coordinateFormatter.format(this.animationPanel.getWorldCoordinate().getY()) + ")";
        this.coordinateField.setText("Mouse: " + worldPoint);
        int requiredWidth = this.coordinateField.getGraphics().getFontMetrics().stringWidth(this.coordinateField.getText());
        if (this.coordinateField.getPreferredSize().width < requiredWidth)
        {
            Dimension requiredSize = new Dimension(requiredWidth, this.coordinateField.getPreferredSize().height);
            this.coordinateField.setPreferredSize(requiredSize);
            this.coordinateField.setMinimumSize(requiredSize);
            Container parent = this.coordinateField.getParent();
            parent.setPreferredSize(requiredSize);
            parent.setMinimumSize(requiredSize);
            parent.revalidate();
        }
        this.coordinateField.repaint();
    }

    /**
     * Return the coordinate formatter for x and y of the world coordinates.
     * @return NumberFormat; the coordinate formatter
     */
    public NumberFormat getCoordinateFormatter()
    {
        return this.coordinateFormatter;
    }

    /**
     * Set the coordinate formatter for x and y of the world coordinates.
     * @param coordinateFormatter NumberFormat; the new coordinate formatter
     */
    public void setCoordinateFormatter(final NumberFormat coordinateFormatter)
    {
        this.coordinateFormatter = coordinateFormatter;
    }

    /**
     * UpdateTimer class to update the coordinate on the screen.
     */
    protected class UpdateTimer extends Thread
    {
        /** application closing? */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        protected boolean closing = false;

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            Window window = null;
            while (!this.closing)
            {
                // register the frame when the whole application has been built and catch the closing event
                if (window == null)
                {
                    window = SwingUtilities.getWindowAncestor(InfoTextPanel.this);
                    if (window != null)
                    {
                        window.addWindowListener(new WindowAdapter()
                        {
                            /** {@inheritDoc} */
                            @Override
                            public void windowClosing(final WindowEvent e)
                            {
                                UpdateTimer.this.closing = true;
                                super.windowClosing(e);
                            }

                        });
                    }
                }

                if (isShowing())
                {
                    updateWorldCoordinate();
                }
                try
                {
                    Thread.sleep(50); // 20 times per second
                }
                catch (InterruptedException exception)
                {
                    // do nothing
                }
            }
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "UpdateTimer thread for InfoTextPanel";
        }
    }

}
