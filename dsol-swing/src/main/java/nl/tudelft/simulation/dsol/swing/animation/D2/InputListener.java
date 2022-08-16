package nl.tudelft.simulation.dsol.swing.animation.D2;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import nl.tudelft.simulation.dsol.swing.animation.D2.actions.PanDownAction;
import nl.tudelft.simulation.dsol.swing.animation.D2.actions.PanLeftAction;
import nl.tudelft.simulation.dsol.swing.animation.D2.actions.PanRightAction;
import nl.tudelft.simulation.dsol.swing.animation.D2.actions.PanUpAction;
import nl.tudelft.simulation.dsol.swing.introspection.gui.IntrospectionDialog;

/**
 * The InputListener for the AnimationPanel. Some of the actions are delegated to the AnimationPanel or to other panels through
 * firing an event.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://www.peter-jacobs.com">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InputListener implements MouseListener, MouseWheelListener, MouseMotionListener, KeyListener
{
    /** the panel to use. */
    private final AnimationPanel panel;

    /** the mouseClicked point in screen coordinates. */
    private Point2D mouseClicked = null;

    /** the last time an ANIMATION_MOUSE_MOVE_EVENT has been fired. */
    private long timeLastAnimationMouseMoveEvent;

    /** the minimum time between two ANIMATION_MOUSE_MOVE_EVENTs in milliseconds. */
    private long timeBetweenAnimationMouseMoveEventsMs = 10; // 100 Hz

    /**
     * constructs a new InputListener.
     * @param panel AnimationPanel; the panel
     */
    public InputListener(final AnimationPanel panel)
    {
        this.panel = panel;
    }

    /** {@inheritDoc} */
    @Override
    public void mouseClicked(final MouseEvent e)
    {
        this.panel.requestFocus();
        this.mouseClicked = e.getPoint();
        if (!e.isPopupTrigger() && !e.isConsumed()) // do not select when ctrl is clicked
        {
            Object selected = this.panel.getSelectedObject(this.panel.getSelectedObjects(e.getPoint()));
            if (selected != null)
            {
                new IntrospectionDialog(selected);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mousePressed(final MouseEvent e)
    {
        this.panel.requestFocus();
        this.mouseClicked = e.getPoint();
        if (e.isPopupTrigger())
        {
            this.panel.popup(e);
            return;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseReleased(final MouseEvent e)
    {
        if (e.isPopupTrigger())
        {
            this.panel.popup(e);
        }
        else
        {
            // Pan if either shift is down or the left mouse button is used.
            if ((e.isShiftDown()) || (e.getButton() == MouseEvent.BUTTON1))
            {
                this.panel.pan(this.mouseClicked, e.getPoint());
                this.panel.repaint();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseEntered(final MouseEvent e)
    {
        // Nothing to be done.
    }

    /** {@inheritDoc} */
    @Override
    public void mouseExited(final MouseEvent e)
    {
        // Nothing to be done
    }

    /** {@inheritDoc} */
    @Override
    public void mouseWheelMoved(final MouseWheelEvent e)
    {
        // Use mouse wheel to zoom
        int amount = e.getUnitsToScroll();
        if (amount > 0)
        {
            this.panel.zoom(GridPanel.ZOOMFACTOR, e.getX(), e.getY());
        }
        else if (amount < 0)
        {
            this.panel.zoom(1.0 / GridPanel.ZOOMFACTOR, e.getX(), e.getY());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseDragged(final MouseEvent e)
    {
        if (e.isShiftDown())
        {
            this.panel.setDragLine(e.getPoint(), this.mouseClicked);
        }
        this.panel.repaint();
    }

    /** {@inheritDoc} */
    @Override
    public void mouseMoved(final MouseEvent mouseEvent)
    {
        long time = System.currentTimeMillis();
        if (time - this.timeLastAnimationMouseMoveEvent > this.timeBetweenAnimationMouseMoveEventsMs)
        {
            this.panel.mouseMoved(mouseEvent.getPoint());
            this.timeLastAnimationMouseMoveEvent = time;
        }
    }

    /**
     * Return the minimum time between two successive animation mouse move events in milliseconds.
     * @return long; the minimum time between two successive animation mouse move events in milliseconds
     */
    public long getTimeBetweenAnimationMouseMoveEventsMs()
    {
        return this.timeBetweenAnimationMouseMoveEventsMs;
    }

    /**
     * Set the minimum time between two successive animation mouse move events in milliseconds.
     * @param timeBetweenAnimationMouseMoveEventsMs long; set the minimum time between two successive animation mouse move
     *            events in milliseconds
     */
    public void setTimeBetweenAnimationMouseMoveEventsMs(final long timeBetweenAnimationMouseMoveEventsMs)
    {
        this.timeBetweenAnimationMouseMoveEventsMs = timeBetweenAnimationMouseMoveEventsMs;
    }

    /** {@inheritDoc} */
    @Override
    public void keyPressed(final KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_LEFT:
                new PanLeftAction(this.panel).actionPerformed(new ActionEvent(this, 0, "LEFT"));
                break;
            case KeyEvent.VK_RIGHT:
                new PanRightAction(this.panel).actionPerformed(new ActionEvent(this, 0, "RIGHT"));
                break;
            case KeyEvent.VK_UP:
                new PanUpAction(this.panel).actionPerformed(new ActionEvent(this, 0, "UP"));
                break;
            case KeyEvent.VK_DOWN:
                new PanDownAction(this.panel).actionPerformed(new ActionEvent(this, 0, "DOWN"));
                break;
            case KeyEvent.VK_MINUS:
                this.panel.zoom(GridPanel.ZOOMFACTOR, this.panel.getWidth() / 2, this.panel.getHeight() / 2);
                break;
            case KeyEvent.VK_PLUS:
                this.panel.zoom(1.0 / GridPanel.ZOOMFACTOR, this.panel.getWidth() / 2, this.panel.getHeight() / 2);
                break;
            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public void keyReleased(final KeyEvent e)
    {
        // nothing to be done
    }

    /** {@inheritDoc} */
    @Override
    public void keyTyped(final KeyEvent e)
    {
        // nothing to be done
    }

}
