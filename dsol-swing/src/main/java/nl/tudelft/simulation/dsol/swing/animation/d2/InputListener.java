package nl.tudelft.simulation.dsol.swing.animation.d2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import nl.tudelft.simulation.dsol.swing.introspection.gui.IntrospectionDialog;

/**
 * The InputListener for the AnimationPanel. Some of the actions are delegated to the AnimationPanel or to other panels through
 * firing an event.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://www.peter-jacobs.com">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class InputListener implements MouseListener, MouseWheelListener, MouseMotionListener, KeyListener
{
    /** the panel to use. */
    private final VisualizationPanel panel;

    /** the mouseClicked point in screen coordinates. */
    private Point2D mouseClicked = null;

    /** the last time an ANIMATION_MOUSE_MOVE_EVENT has been fired. */
    private long timeLastAnimationMouseMoveEvent;

    /** the minimum time between two ANIMATION_MOUSE_MOVE_EVENTs in milliseconds. */
    private long timeBetweenAnimationMouseMoveEventsMs = 10; // 100 Hz

    /**
     * constructs a new InputListener.
     * @param panel the panel
     */
    public InputListener(final VisualizationPanel panel)
    {
        this.panel = panel;
    }

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

    @Override
    public void mouseEntered(final MouseEvent e)
    {
        // Nothing to be done.
    }

    @Override
    public void mouseExited(final MouseEvent e)
    {
        // Nothing to be done
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e)
    {
        // Use mouse wheel to zoom
        int amount = e.getUnitsToScroll();
        if (amount > 0)
        {
            if (e.isShiftDown())
                this.panel.zoomY(VisualizationPanel.ZOOMFACTOR, e.getX(), e.getY());
            else if (e.isAltDown())
                this.panel.zoomX(VisualizationPanel.ZOOMFACTOR, e.getX(), e.getY());
            else if (e.isControlDown())
                this.panel.rotate(1.0, e.getX(), e.getY());
            else
                this.panel.zoom(VisualizationPanel.ZOOMFACTOR, e.getX(), e.getY());
        }
        else if (amount < 0)
        {
            if (e.isShiftDown())
                this.panel.zoomY(1.0 / VisualizationPanel.ZOOMFACTOR, e.getX(), e.getY());
            else if (e.isAltDown())
                this.panel.zoomX(1.0 / VisualizationPanel.ZOOMFACTOR, e.getX(), e.getY());
            else if (e.isControlDown())
                this.panel.rotate(-1.0, e.getX(), e.getY());
            else
                this.panel.zoom(1.0 / VisualizationPanel.ZOOMFACTOR, e.getX(), e.getY());
        }
        e.consume();
    }

    @Override
    public void mouseDragged(final MouseEvent e)
    {
        if (e.isShiftDown())
        {
            this.panel.setDragLine(e.getPoint(), this.mouseClicked);
        }
        this.panel.repaint();
        e.consume();
    }

    @Override
    public void mouseMoved(final MouseEvent mouseEvent)
    {
        long time = System.currentTimeMillis();
        if (time - this.timeLastAnimationMouseMoveEvent > this.timeBetweenAnimationMouseMoveEventsMs)
        {
            this.panel.mouseMoved(mouseEvent.getPoint());
            this.timeLastAnimationMouseMoveEvent = time;
        }
        mouseEvent.consume();
    }

    /**
     * Return the minimum time between two successive animation mouse move events in milliseconds.
     * @return the minimum time between two successive animation mouse move events in milliseconds
     */
    public long getTimeBetweenAnimationMouseMoveEventsMs()
    {
        return this.timeBetweenAnimationMouseMoveEventsMs;
    }

    /**
     * Set the minimum time between two successive animation mouse move events in milliseconds.
     * @param timeBetweenAnimationMouseMoveEventsMs set the minimum time between two successive animation mouse move
     *            events in milliseconds
     */
    public void setTimeBetweenAnimationMouseMoveEventsMs(final long timeBetweenAnimationMouseMoveEventsMs)
    {
        this.timeBetweenAnimationMouseMoveEventsMs = timeBetweenAnimationMouseMoveEventsMs;
    }

    @Override
    public void keyPressed(final KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_LEFT:
                this.panel.pan(VisualizationPanel.RIGHT, 0.1);
                break;
            case KeyEvent.VK_RIGHT:
                this.panel.pan(VisualizationPanel.LEFT, 0.1);
                break;
            case KeyEvent.VK_UP:
                this.panel.pan(VisualizationPanel.DOWN, 0.1);
                break;
            case KeyEvent.VK_DOWN:
                this.panel.pan(VisualizationPanel.UP, 0.1);
                break;
            case KeyEvent.VK_MINUS:
                if (e.isControlDown()) 
                    this.panel.zoomY(VisualizationPanel.ZOOMFACTOR, 0, this.panel.getHeight() / 2);
                else
                    this.panel.zoom(VisualizationPanel.ZOOMFACTOR, this.panel.getWidth() / 2, this.panel.getHeight() / 2);
                break;
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_EQUALS:
                if (e.isControlDown()) 
                    this.panel.zoomY(1.0 / VisualizationPanel.ZOOMFACTOR, 0, this.panel.getHeight() / 2);
                else
                    this.panel.zoom(1.0 / VisualizationPanel.ZOOMFACTOR, this.panel.getWidth() / 2, this.panel.getHeight() / 2);
                break;
            case KeyEvent.VK_Z:
                if (e.isControlDown() && e.isShiftDown())
                    this.panel.resetZoomY();
                break;
            default:
        }
    }

    @Override
    public void keyReleased(final KeyEvent e)
    {
        // nothing to be done
    }

    @Override
    public void keyTyped(final KeyEvent e)
    {
        // nothing to be done
    }

}
