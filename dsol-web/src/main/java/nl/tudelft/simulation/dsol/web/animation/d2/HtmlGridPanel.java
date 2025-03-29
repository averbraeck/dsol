package nl.tudelft.simulation.dsol.web.animation.d2;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.awt.image.ImageObserver;
import java.text.NumberFormat;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.d2.RenderableScale;
import nl.tudelft.simulation.dsol.web.animation.HtmlGraphics2D;

/**
 * The GridPanel introduces the gridPanel.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="mailto:nlang@fbk.eur.nl">Niels Lang </a>, <a href="http://www.peter-jacobs.com">Peter Jacobs </a>
 */
public class HtmlGridPanel implements ImageObserver
{
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

    /** the extent of this panel. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Bounds2d extent = null;

    /** the extent of this panel. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Bounds2d homeExtent = null;

    /** show the grid. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean showGrid = true;

    /** the gridSize for the X-direction in world Units. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double gridSizeX = 100.0;

    /** the gridSize for the Y-direction in world Units. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double gridSizeY = 100.0;

    /** gridColor. */
    private Color gridColor = Color.BLACK;

    /** the formatter to use. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected NumberFormat formatter = NumberFormat.getInstance();

    /** the last computed Dimension. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Dimension lastDimension = null;

    /** the last stored screen dimensions for zoom-in, zoom-out. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Dimension lastScreen = null;

    /** the last stored x-scale for zoom-in, zoom-out. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Double lastXScale = null;

    /** the last stored y-scale for zoom-in, zoom-out. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Double lastYScale = null;

    /** the last computed Dimension. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Dimension size = null;

    /** the last computed Dimension. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Dimension preferredSize = null;

    /** the last known world coordinate of the mouse. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Point2d worldCoordinate = new Point2d(0, 0);

    /** whether to show a tooltip with the coordinates or not. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean showToolTip = true;

    /** the background color. */
    private Color background;

    /** The tooltip text which shows the coordinates. */
    private String toolTipText = "";

    /** Whether the panel is showing or not. */
    private boolean showing = true;

    /** the current font. */
    private Font currentFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

    /** the canvas to determine the font metrics. */
    private Canvas canvas = new Canvas();

    /** the HtmlGraphics2D 'shadow' canvas. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected HtmlGraphics2D htmlGraphics2D;

    /** dirty flag. */
    private boolean dirty = false;

    /** the renderable scale (X/Y ratio) to use. */
    private RenderableScale renderableScale;

    /**
     * constructs a new GridPanel.
     * @param extent the extent to show.
     */
    public HtmlGridPanel(final Bounds2d extent)
    {
        this(extent, new Dimension(600, 600));
    }

    /**
     * constructs a new GridPanel.
     * @param homeExtent the initial extent.
     * @param size the size of the panel in pixels.
     */
    public HtmlGridPanel(final Bounds2d homeExtent, final Dimension size)
    {
        this.renderableScale = new RenderableScale();
        this.htmlGraphics2D = new HtmlGraphics2D();
        this.extent = homeExtent;
        this.homeExtent = homeExtent;
        this.setBackground(Color.WHITE);
        this.setPreferredSize(size);
        this.size = (Dimension) size.clone();
        this.lastDimension = this.getSize();
        this.lastScreen = this.getSize();
        setExtent(homeExtent);
    }

    /**
     * Return the set of drawing commands.
     * @return the set of drawing commands
     */
    public String getDrawingCommands()
    {
        this.htmlGraphics2D.clearCommand();
        this.paintComponent(this.htmlGraphics2D);
        return this.htmlGraphics2D.closeAndGetCommands();
    }

    /**
     * Draw the grid.
     * @param g the virtual Graphics2D canvas to enable writing to the browser
     */
    public void paintComponent(final HtmlGraphics2D g)
    {
        if (!this.getSize().equals(this.lastDimension))
        {
            this.lastDimension = this.getSize();
            this.extent = computeVisibleExtent(this.extent);
        }
        if (this.showGrid)
        { this.drawGrid(g); }
    }

    /**
     * show the grid?
     * @param bool true/false
     */
    public synchronized void showGrid(final boolean bool)
    {
        this.showGrid = bool;
        this.repaint();
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
     * returns the extent of this panel.
     * @param extent the new extent
     */
    public void setExtent(final Bounds2d extent)
    {
        if (this.lastScreen != null)
        {
            // this prevents zoom being undone when resizing the screen afterwards
            this.lastXScale = this.getRenderableScale().getXScale(extent, this.lastScreen);
            this.lastYScale = this.getRenderableScale().getYScale(extent, this.lastScreen);
        }
        this.extent = extent;
        this.repaint();
    }

    /**
     * Set the world coordinates based on a mouse move.
     * @param point the x,y world coordinates
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
     * @param showToolTip set showToolTip
     */
    public synchronized void setShowToolTip(final boolean showToolTip)
    {
        this.showToolTip = showToolTip;
    }

    /**
     * pans the panel in a specified direction.
     * @param direction the direction
     * @param percentage the percentage
     */
    public synchronized void pan(final int direction, final double percentage)
    {
        if (percentage <= 0 || percentage > 1.0)
        { throw new IllegalArgumentException("percentage<=0 || >1.0"); }
        switch (direction)
        {
            case LEFT:
                this.extent = new Bounds2d(this.extent.getMinX() - percentage * this.extent.getDeltaX(),
                        this.extent.getMaxX() - percentage * this.extent.getDeltaX(), this.extent.getMinY(),
                        this.extent.getMaxY());
                break;
            case RIGHT:
                this.extent = new Bounds2d(this.extent.getMinX() + percentage * this.extent.getDeltaX(),
                        this.extent.getMaxX() + percentage * this.extent.getDeltaX(), this.extent.getMinY(),
                        this.extent.getMaxY());
                break;
            case UP:
                this.extent = new Bounds2d(this.extent.getMinX(), this.extent.getMaxX(),
                        this.extent.getMinY() + percentage * this.extent.getDeltaY(),
                        this.extent.getMaxY() + percentage * this.extent.getDeltaY());
                break;
            case DOWN:
                this.extent = new Bounds2d(this.extent.getMinX(), this.extent.getMaxX(),
                        this.extent.getMinY() - percentage * this.extent.getDeltaY(),
                        this.extent.getMaxY() - percentage * this.extent.getDeltaY());
                break;
            default:
                throw new IllegalArgumentException("direction unkown");
        }
        this.repaint();
    }

    /**
     * resets the panel to its original extent.
     */
    public synchronized void home()
    {
        this.extent = computeVisibleExtent(this.homeExtent);
        this.repaint();
    }

    /**
     * @return Returns the showGrid.
     */
    public boolean isShowGrid()
    {
        return this.showGrid;
    }

    /**
     * @param showGrid The showGrid to set.
     */
    public void setShowGrid(final boolean showGrid)
    {
        this.showGrid = showGrid;
    }

    /**
     * Return the current grid color for this VisualizationPanel.
     * @return the current grid color
     */
    public Color getGridColor()
    {
        return this.gridColor;
    }

    /**
     * Set a new grid color for this VisualizationPanel
     * @param gridColor the new grid color
     */
    public void setGridColor(final Color gridColor)
    {
        this.gridColor = gridColor;
    }

    /**
     * zooms in/out.
     * @param factor The zoom factor
     */
    public synchronized void zoom(final double factor)
    {
        zoom(factor, (int) (this.getWidth() / 2.0), (int) (this.getHeight() / 2.0));
    }

    /**
     * zooms in/out.
     * @param factor The zoom factor
     * @param mouseX x-position of the mouse around which we zoom
     * @param mouseY y-position of the mouse around which we zoom
     */
    public synchronized void zoom(final double factor, final int mouseX, final int mouseY)
    {
        Point2d mwc = this.renderableScale.getWorldCoordinates(new Point2D.Double(mouseX, mouseY), this.extent, this.getSize());
        double minX = mwc.getX() - (mwc.getX() - this.extent.getMinX()) * factor;
        double minY = mwc.getY() - (mwc.getY() - this.extent.getMinY()) * factor;
        double w = this.extent.getDeltaX() * factor;
        double h = this.extent.getDeltaY() * factor;

        this.extent = new Bounds2d(minX, minX + w, minY, minY + h);
        this.repaint();
    }

    /**
     * Added to make sure the recursive render-call calls THIS render method instead of a potential super-class defined
     * 'paintComponent' render method.
     * @param g the graphics object
     */
    protected synchronized void drawGrid(final Graphics g)
    {
        // we prepare the graphics object for the grid
        g.setFont(g.getFont().deriveFont(11.0f));
        g.setColor(this.gridColor);
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
            { break; }
        }

        count = 0;
        while (gridSizePixelsX > 10 * 40)
        {
            int maximumNumberOfDigits = (int) Math.max(0, 2 + Math.ceil(Math.log(1 / this.gridSizeX) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            this.gridSizeX = this.gridSizeX / 10;
            gridSizePixelsX = (int) Math.round(this.gridSizeX / scaleX);
            if (count++ > 10)
            { break; }
        }

        int gridSizePixelsY = (int) Math.round(this.gridSizeY / scaleY);
        while (gridSizePixelsY < 40)
        {
            this.gridSizeY = 10 * this.gridSizeY;
            int maximumNumberOfDigits = (int) Math.max(0, 1 + Math.ceil(Math.log(1 / this.gridSizeY) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            gridSizePixelsY = (int) Math.round(this.gridSizeY / scaleY);
            if (count++ > 10)
            { break; }
        }

        count = 0;
        while (gridSizePixelsY > 10 * 40)
        {
            int maximumNumberOfDigits = (int) Math.max(0, 2 + Math.ceil(Math.log(1 / this.gridSizeY) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            this.gridSizeY = this.gridSizeY / 10;
            gridSizePixelsY = (int) Math.round(this.gridSizeY / scaleY);
            if (count++ > 10)
            { break; }
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
     * Repaint the shadow canvas.
     */
    public void repaint()
    {
        // repaint does not do any painting -- information is pulled from the browser
        this.dirty = true;
    }

    /**
     * @return size
     */
    public Dimension getSize()
    {
        return this.size;
    }

    /**
     * @param size set size
     */
    public void setSize(final Dimension size)
    {
        this.size = size;
    }

    /**
     * @return background
     */
    public Color getBackground()
    {
        return this.background;
    }

    /**
     * @param background set background
     */
    public void setBackground(final Color background)
    {
        this.background = background;
    }

    /**
     * @return width
     */
    public int getWidth()
    {
        return this.size.width;
    }

    /**
     * @return height
     */
    public int getHeight()
    {
        return this.size.height;
    }

    /**
     * @return preferredSize
     */
    public Dimension getPreferredSize()
    {
        return this.preferredSize;
    }

    /**
     * @param preferredSize set preferredSize
     */
    public void setPreferredSize(final Dimension preferredSize)
    {
        this.preferredSize = preferredSize;
    }

    /**
     * @return toolTipText
     */
    public String getToolTipText()
    {
        return this.toolTipText;
    }

    /**
     * @param toolTipText set toolTipText
     */
    public void setToolTipText(final String toolTipText)
    {
        this.toolTipText = toolTipText;
    }

    /**
     * @return showing
     */
    public boolean isShowing()
    {
        return this.showing;
    }

    /**
     * @param showing set showing
     */
    public void setShowing(final boolean showing)
    {
        this.showing = showing;
    }

    /**
     * @return font
     */
    public Font getFont()
    {
        return this.currentFont;
    }

    /**
     * @param font set font
     */
    public void setFont(final Font font)
    {
        this.currentFont = font;
    }

    /**
     * @param font the font to calculate the fontmetrics for
     * @return fontMetrics
     */
    public FontMetrics getFontMetrics(final Font font)
    {
        return this.canvas.getFontMetrics(font);
    }

    /**
     * @return dirty
     */
    public boolean isDirty()
    {
        return this.dirty;
    }

    @Override
    public boolean imageUpdate(final Image img, final int infoflags, final int x, final int y, final int width,
            final int height)
    {
        return false;
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

    /**
     * Computes the visible extent, while preserving zoom scale, otherwise dragging the split screen may pump up the zoom factor
     * @param extent the extent to use
     * @return a new extent or null if parameters are null or screen is invalid (width / height &lt;= 0)
     */
    public Bounds2d computeVisibleExtent(final Bounds2d extent)
    {
        Dimension screen = getSize();
        double xScale = this.renderableScale.getXScale(extent, screen);
        double yScale = this.renderableScale.getYScale(extent, screen);
        Bounds2d result;
        if (this.lastYScale != null && yScale == this.lastYScale)
        {
            result = new Bounds2d(extent.midPoint().getX() - 0.5 * screen.getWidth() * yScale,
                    extent.midPoint().getX() + 0.5 * screen.getWidth() * yScale, extent.getMinY(), extent.getMaxY());
            xScale = yScale;
        }
        else if (this.lastXScale != null && xScale == this.lastXScale)
        {
            result = new Bounds2d(extent.getMinX(), extent.getMaxX(),
                    extent.midPoint().getY() - 0.5 * screen.getHeight() * xScale * this.renderableScale.getYScaleRatio(),
                    extent.midPoint().getY() + 0.5 * screen.getHeight() * xScale * this.renderableScale.getYScaleRatio());
            yScale = xScale;
        }
        else
        {
            double scale = this.lastXScale == null ? Math.min(xScale, yScale)
                    : this.lastXScale * this.lastScreen.getWidth() / screen.getWidth();
            result = new Bounds2d(extent.midPoint().getX() - 0.5 * screen.getWidth() * scale,
                    extent.midPoint().getX() + 0.5 * screen.getWidth() * scale,
                    extent.midPoint().getY() - 0.5 * screen.getHeight() * scale * this.renderableScale.getYScaleRatio(),
                    extent.midPoint().getY() + 0.5 * screen.getHeight() * scale * this.renderableScale.getYScaleRatio());
            yScale = scale;
            xScale = scale;
        }
        this.lastXScale = xScale;
        this.lastYScale = yScale;
        this.lastScreen = screen;
        return result;
    }

}
