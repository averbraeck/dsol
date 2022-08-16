package nl.tudelft.simulation.dsol.swing.animation.D2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.text.NumberFormat;

import javax.swing.JPanel;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.D2.RenderableScale;

/**
 * The GridPanel implements the basic functions to show animations: toggle grid on/off, zooming, panning, translation between 
 * world coordinates and screen coordinates, and changing the displayed extent such as the home extent.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author Niels Lang
 * @author <a href="http://www.peter-jacobs.com">Peter Jacobs </a>
 */
public abstract class GridPanel extends JPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the UP directions for moving/zooming. */
    public static final int UP = 1;

    /** the DOWN directions for moving/zooming. */
    public static final int DOWN = 2;

    /** the LEFT directions for moving/zooming. */
    public static final int LEFT = 3;

    /** the RIGHT directions for moving/zooming. */
    public static final int RIGHT = 4;

    /** the ZOOM factor. */
    public static final double ZOOMFACTOR = 1.2;

    /** gridColor. */
    protected static final Color GRIDCOLOR = Color.BLACK;

    /** the extent of this panel. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    private Bounds2d extent = null;

    /** the initial and default extent of this panel. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    private Bounds2d homeExtent = null;

    /** show the grid. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean showGrid = true;

    /** the gridSize for the X-direction in world Units. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double gridSizeX = 100.0;

    /** the gridSize for the Y-direction in world Units. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double gridSizeY = 100.0;

    /** the formatter to use. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected NumberFormat formatter = NumberFormat.getInstance();

    /** the last computed Dimension. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Dimension lastDimension = null;

    /** the last known world coordinate of the mouse. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Point2d worldCoordinate = new Point2d(0.0, 0.0);

    /** whether to show a tooltip with the coordinates or not. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean showToolTip = true;

    /** the renderable scale (X/Y ratio) to use. */
    private RenderableScale renderableScale;

    /**
     * constructs a new GridPanel.
     * @param homeExtent Bounds2d; the initial extent.
     */
    public GridPanel(final Bounds2d homeExtent)
    {
        this.renderableScale = new RenderableScale();
        this.homeExtent = homeExtent;
        this.setBackground(Color.WHITE);
        this.lastDimension = this.getSize();
        setExtent(homeExtent);
    }

    /**
     * returns the extent of this panel.
     * @return Bounds2d
     */
    public Bounds2d getExtent()
    {
        return this.extent;
    }

    /**
     * set a new extent for this panel.
     * @param extent Bounds2d; set a new extent
     */
    public void setExtent(final Bounds2d extent)
    {
        this.extent = extent;
        this.repaint();
    }

    /** {@inheritDoc} */
    @Override
    public void paintComponent(final Graphics g)
    {
        super.paintComponent(g);

        if (!this.getSize().equals(this.lastDimension))
        {
            this.lastDimension = this.getSize();
            setExtent(this.renderableScale.computeVisibleExtent(this.extent, this.getSize()));
        }
        if (this.showGrid)
        {
            this.drawGrid(g);
        }
    }

    /**
     * show the grid?
     * @param bool boolean; true/false
     */
    public synchronized void showGrid(final boolean bool)
    {
        this.showGrid = bool;
        this.repaint();
    }

    /**
     * Set the world coordinates based on a mouse move.
     * @param point Point2D; the x,y world coordinates
     */
    public synchronized void setWorldCoordinate(final Point2d point)
    {
        this.worldCoordinate = point;
    }

    /**
     * @return worldCoordinate
     */
    public synchronized Point2d getWorldCoordinate()
    {
        return this.worldCoordinate;
    }

    /**
     * Display a tooltip with the last known world coordinates of the mouse, in case the tooltip should be displayed.
     */
    public synchronized void displayWorldCoordinateToolTip()
    {
        if (this.showToolTip)
        {
            String worldPoint = "(x=" + this.formatter.format(this.worldCoordinate.getX()) + " ; y="
                    + this.formatter.format(this.worldCoordinate.getY()) + ")";
            setToolTipText(worldPoint);
        }
    }

    /**
     * @return showToolTip
     */
    public synchronized boolean isShowToolTip()
    {
        return this.showToolTip;
    }

    /**
     * @param showToolTip boolean; set showToolTip
     */
    public synchronized void setShowToolTip(final boolean showToolTip)
    {
        this.showToolTip = showToolTip;
    }

    /**
     * pans the panel in a specified direction.
     * @param direction int; the direction
     * @param percentage double; the percentage
     */
    public synchronized void pan(final int direction, final double percentage)
    {
        if (percentage <= 0 || percentage > 1.0)
        {
            throw new IllegalArgumentException("percentage<=0 || >1.0");
        }
        switch (direction)
        {
            case LEFT:
                setExtent(new Bounds2d(this.extent.getMinX() - percentage * this.extent.getDeltaX(),
                        this.extent.getMaxX() - percentage * this.extent.getDeltaX(), this.extent.getMinY(),
                        this.extent.getMaxY()));
                break;
            case RIGHT:
                setExtent(new Bounds2d(this.extent.getMinX() + percentage * this.extent.getDeltaX(),
                        this.extent.getMaxX() + percentage * this.extent.getDeltaX(), this.extent.getMinY(),
                        this.extent.getMaxY()));
                break;
            case UP:
                setExtent(new Bounds2d(this.extent.getMinX(), this.extent.getMaxX(),
                        this.extent.getMinY() + percentage * this.extent.getDeltaY(),
                        this.extent.getMaxY() + percentage * this.extent.getDeltaY()));
                break;
            case DOWN:
                setExtent(new Bounds2d(this.extent.getMinX(), this.extent.getMaxX(),
                        this.extent.getMinY() - percentage * this.extent.getDeltaY(),
                        this.extent.getMaxY() - percentage * this.extent.getDeltaY()));
                break;
            default:
                throw new IllegalArgumentException("direction unkown");
        }
    }

    /**
     * resets the panel to its original extent.
     */
    public synchronized void home()
    {
        setExtent(this.renderableScale.computeVisibleExtent(this.homeExtent, this.getSize()));
    }

    /**
     * @return Returns the showGrid.
     */
    public boolean isShowGrid()
    {
        return this.showGrid;
    }

    /**
     * @param showGrid boolean; The showGrid to set.
     */
    public void setShowGrid(final boolean showGrid)
    {
        this.showGrid = showGrid;
    }

    /**
     * zooms in/out.
     * @param factor double; The zoom factor
     */
    public synchronized void zoom(final double factor)
    {
        zoom(factor, (int) (this.getWidth() / 2.0), (int) (this.getHeight() / 2.0));
    }

    /**
     * zooms in/out.
     * @param factor double; The zoom factor
     * @param mouseX int; x-position of the mouse around which we zoom
     * @param mouseY int; y-position of the mouse around which we zoom
     */
    public synchronized void zoom(final double factor, final int mouseX, final int mouseY)
    {
        Point2d mwc = this.renderableScale.getWorldCoordinates(new Point2D.Double(mouseX, mouseY), this.extent, this.getSize());
        double minX = mwc.getX() - (mwc.getX() - this.extent.getMinX()) * factor;
        double minY = mwc.getY() - (mwc.getY() - this.extent.getMinY()) * factor;
        double w = this.extent.getDeltaX() * factor;
        double h = this.extent.getDeltaY() * factor;

        setExtent(new Bounds2d(minX, minX + w, minY, minY + h));
    }

    /**
     * Added to make sure the recursive render-call calls THIS render method instead of a potential super-class defined
     * 'paintComponent' render method.
     * @param g Graphics; the graphics object
     */
    protected synchronized void drawGrid(final Graphics g)
    {
        // we prepare the graphics object for the grid
        g.setFont(g.getFont().deriveFont(11.0f));
        g.setColor(GRIDCOLOR);
        double scaleX = this.renderableScale.getXScale(this.extent, this.getSize());
        double scaleY = this.renderableScale.getYScale(this.extent, this.getSize());

        int count = 0;
        int gridSizePixelsX = (int) Math.round(this.gridSizeX / scaleX);
        while (gridSizePixelsX < 40)
        {
            this.gridSizeX = 10 * this.gridSizeX;
            int maximumNumberOfDigits = (int) Math.max(0, 1 + Math.ceil(Math.log(1 / this.gridSizeX) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            gridSizePixelsX = (int) Math.round(this.gridSizeX / scaleX);
            if (count++ > 10)
            {
                break;
            }
        }

        count = 0;
        while (gridSizePixelsX > 10 * 40)
        {
            int maximumNumberOfDigits = (int) Math.max(0, 2 + Math.ceil(Math.log(1 / this.gridSizeX) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            this.gridSizeX = this.gridSizeX / 10;
            gridSizePixelsX = (int) Math.round(this.gridSizeX / scaleX);
            if (count++ > 10)
            {
                break;
            }
        }

        int gridSizePixelsY = (int) Math.round(this.gridSizeY / scaleY);
        while (gridSizePixelsY < 40)
        {
            this.gridSizeY = 10 * this.gridSizeY;
            int maximumNumberOfDigits = (int) Math.max(0, 1 + Math.ceil(Math.log(1 / this.gridSizeY) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            gridSizePixelsY = (int) Math.round(this.gridSizeY / scaleY);
            if (count++ > 10)
            {
                break;
            }
        }

        count = 0;
        while (gridSizePixelsY > 10 * 40)
        {
            int maximumNumberOfDigits = (int) Math.max(0, 2 + Math.ceil(Math.log(1 / this.gridSizeY) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            this.gridSizeY = this.gridSizeY / 10;
            gridSizePixelsY = (int) Math.round(this.gridSizeY / scaleY);
            if (count++ > 10)
            {
                break;
            }
        }

        // Let's draw the vertical lines
        double mod = this.extent.getMinX() % this.gridSizeX;
        int x = (int) -Math.round(mod / scaleX);
        while (x < this.getWidth())
        {
            Point2d point = this.renderableScale.getWorldCoordinates(new Point2D.Double(x, 0), this.extent, this.getSize());
            if (point != null)
            {
                String label = this.formatter.format(Math.round(point.getX() / this.gridSizeX) * this.gridSizeX);
                double labelWidth = this.getFontMetrics(this.getFont()).getStringBounds(label, g).getWidth();
                if (x > labelWidth + 4)
                {
                    g.drawLine(x, 15, x, this.getHeight());
                    g.drawString(label, (int) Math.round(x - 0.5 * labelWidth), 11);
                }
            }
            x = x + gridSizePixelsX;
        }

        // Let's draw the horizontal lines
        mod = Math.abs(this.extent.getMinY()) % this.gridSizeY;
        int y = (int) Math.round(this.getSize().getHeight() - (mod / scaleY));
        while (y > 15)
        {
            Point2d point = this.renderableScale.getWorldCoordinates(new Point2D.Double(0, y), this.extent, this.getSize());
            if (point != null)
            {
                String label = this.formatter.format(Math.round(point.getY() / this.gridSizeY) * this.gridSizeY);
                RectangularShape labelBounds = this.getFontMetrics(this.getFont()).getStringBounds(label, g);
                g.drawLine((int) Math.round(labelBounds.getWidth() + 4), y, this.getWidth(), y);
                g.drawString(label, 2, (int) Math.round(y + labelBounds.getHeight() * 0.3));
            }
            y = y - gridSizePixelsY;
        }
    }

    /**
     * @return renderableScale
     */
    public RenderableScale getRenderableScale()
    {
        return this.renderableScale;
    }

    /**
     * @param renderableScale set renderableScale
     */
    public void setRenderableScale(final RenderableScale renderableScale)
    {
        this.renderableScale = renderableScale;
    }

}
