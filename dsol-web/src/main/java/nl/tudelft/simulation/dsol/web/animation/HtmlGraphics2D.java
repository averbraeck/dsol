package nl.tudelft.simulation.dsol.web.animation;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.logger.Cat;

/**
 * HtmlGraphics.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class HtmlGraphics2D extends Graphics2D
{
    /** the current color of the background for drawing. */
    Color background = Color.WHITE;

    /** the current drawing color. */
    Color color = Color.BLACK;

    /** the current font. */
    Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

    /** the drawing canvas. */
    Canvas canvas = new Canvas();

    /** the cached current font properties. */
    FontMetrics fontMetrics = this.canvas.getFontMetrics(this.font);

    /** the current paint. */
    Paint paint = Color.BLACK;

    /** the current stroke. */
    Stroke stroke = new BasicStroke();

    /** TODO: the current rendering hints. */
    RenderingHints renderingHints = new RenderingHints(new LinkedHashMap<Key, Object>());

    /** the current affine transform. */
    AffineTransform affineTransform = new AffineTransform();

    /** TODO: the current composite. What is that? */
    Composite composite = AlphaComposite.Clear;

    /** the commands to send over the channel to the HTML5 code. */
    StringBuffer commands = new StringBuffer();

    /**
     * Clear the commands and put the start tag in.
     */
    public void clearCommand()
    {
        this.commands = new StringBuffer();
        this.commands.append("<animate>\n");
    }

    /**
     * Close the commands and put the end tag in.
     * @return the current set of commands
     */
    public String closeAndGetCommands()
    {
        this.commands.append("</animate>\n");
        return this.commands.toString();
    }

    /**
     * Add a draw command
     * @param drawCommand String; the tag for the draw command
     * @param params Object...; the params for the draw command
     */
    protected void addDraw(String drawCommand, Object... params)
    {
        this.commands.append("<draw>" + drawCommand);
        for (Object param : params)
        {
            this.commands.append("," + param.toString());
        }
        this.commands.append("</draw>\n");
    }

    /**
     * add AffineTransform to the command.
     */
    protected void addAffineTransform()
    {
        this.commands.append(",");
        this.commands.append(this.affineTransform.getScaleX());
        this.commands.append(",");
        this.commands.append(this.affineTransform.getShearY());
        this.commands.append(",");
        this.commands.append(this.affineTransform.getShearX());
        this.commands.append(",");
        this.commands.append(this.affineTransform.getScaleY());
        this.commands.append(",");
        this.commands.append(this.affineTransform.getTranslateX());
        this.commands.append(",");
        this.commands.append(this.affineTransform.getTranslateY());
    }

    /**
     * add Color to the command.
     * @param c Color; the color
     */
    protected void addColor(Color c)
    {
        this.commands.append(",");
        this.commands.append(c.getRed());
        this.commands.append(",");
        this.commands.append(c.getGreen());
        this.commands.append(",");
        this.commands.append(c.getBlue());
        this.commands.append(",");
        this.commands.append(c.getAlpha());
        this.commands.append(",");
        this.commands.append(c.getTransparency());
    }

    /**
     * add font data to the command, font-name, font-size, bold/italic/plain
     */
    protected void addFontData()
    {
        this.commands.append(",");
        String javaFontName = this.font.getFontName().toLowerCase();
        String htmlFontName;
        if (javaFontName.contains("arial") || javaFontName.contains("helvetica") || javaFontName.contains("verdana")
                || javaFontName.contains("tahoma") || javaFontName.contains("segoe") || javaFontName.contains("sans"))
            htmlFontName = "sans-serif";
        else if (javaFontName.contains("times") || javaFontName.contains("cambria") || javaFontName.contains("georgia")
                || javaFontName.contains("serif"))
            htmlFontName = "serif";
        else if (javaFontName.contains("courier") || javaFontName.contains("consol") || javaFontName.contains("mono"))
            htmlFontName = "monospace";
        else
            htmlFontName = "sans-serif";
        this.commands.append(htmlFontName);
        this.commands.append(",");
        this.commands.append(this.font.getSize2D());
        this.commands.append(",");
        if (this.font.isBold())
            this.commands.append("bold");
        else if (this.font.isItalic())
            this.commands.append("italic");
        else
            this.commands.append("plain");
    }

    /**
     * Add fill command, transform.m11(h-scale), transform.m12(h-skew), transform.m21(v-skew), transform.m22(v-scale),
     * transform.dx(h-translate), transform.dy(v-translate), color.r, color.g, color.b, color.alpha, color.transparency,
     * params...
     * @param fillCommand String; the tag to use
     * @param params Object...; the params to send
     */
    protected void addTransformFill(String fillCommand, Object... params)
    {
        this.commands.append("<transformFill>" + fillCommand);
        addAffineTransform();
        if (this.paint instanceof Color)
            addColor((Color) this.paint);
        else
            addColor(this.color);
        for (Object param : params)
        {
            this.commands.append("," + param.toString());
        }
        this.commands.append("</transformFill>\n");
    }

    /**
     * Add command, transform.m11(h-scale), transform.m12(h-skew), transform.m21(v-skew), transform.m22(v-scale),
     * transform.dx(h-translate), transform.dy(v-translate), linecolor.r, linecolor.g, linecolor.b, linecolor.alpha,
     * linecolor.transparency, line-width, params...
     * @param drawCommand String; the tag to use
     * @param params Object...; the params
     */
    protected void addTransformDraw(String drawCommand, Object... params)
    {
        this.commands.append("<transformDraw>" + drawCommand);
        addAffineTransform();
        if (this.paint instanceof Color)
            addColor((Color) this.paint);
        else
            addColor(this.color);
        if (this.stroke instanceof BasicStroke)
            this.commands.append("," + ((BasicStroke) this.stroke).getLineWidth());
        else
            this.commands.append(", 0.1");
        for (Object param : params)
        {
            this.commands.append("," + param.toString());
        }
        this.commands.append("</transformDraw>\n");
    }

    /**
     * adds a float array to the command
     * @param array float[]; the array
     * @param length int; the number of points from the array to write
     */
    private void addFloatArray(final float[] array, final int length)
    {
        for (int i = 0; i < length; i++)
        {
            this.commands.append(", " + array[i]);
        }
    }

    /**
     * adds a double array to the command
     * @param array double[]; the array
     * @param length int; the number of points from the array to write
     */
    private void addDoubleArray(final double[] array, final int length)
    {
        for (int i = 0; i < length; i++)
        {
            this.commands.append(", " + array[i]);
        }
    }

    /**
     * Add a path2D to the command. In case of fill:<br>
     * FILL, transform.m11(h-scale), transform.m12(h-skew), transform.m21(v-skew), transform.m22(v-scale),
     * transform.dx(h-translate), transform.dy(v-translate), fillcolor.r, fillcolor.g, fillcolor.b, fillcolor.alpha,
     * fillcolor.transparency, winding_rule[WIND_EVEN_ODD/WIND_NON_ZERO], COMMAND, coords, COMMAND, coords, ... <br>
     * In case of draw:<br>
     * DRAW, transform.m11(h-scale), transform.m12(h-skew), transform.m21(v-skew), transform.m22(v-scale),
     * transform.dx(h-translate), transform.dy(v-translate), strokecolor.r, strokecolor.g, strokecolor.b, strokecolor.alpha,
     * strokecolor.transparency, line_width, COMMAND, coords, COMMAND, coords, ... <br>
     * where command can be one of the following:<br>
     * - CLOSE, followed by no coordinates<br>
     * - CUBICTO, followed by 3 coordinates (6 numbers)<br>
     * - LINETO, followed by 1 coordinate (2 numbers)<br>
     * - MOVETO, followed by 1 coordinate (2 numbers)<br>
     * - QUADTO, followed by 2 coordinates (4 numbers)<br>
     * @param path Path2D.Float; the path to draw
     * @param fill boolean;
     */
    protected void addTransformPathFloat(Path2D.Float path, boolean fill)
    {
        if (fill)
            this.commands.append("<transformPath>FILL");
        else
            this.commands.append("<transformPath>DRAW");
        addAffineTransform();
        addColor(this.color);
        if (fill)
        {
            if (path.getWindingRule() == Path2D.WIND_EVEN_ODD)
                this.commands.append(",WIND_EVEN_ODD");
            else
                this.commands.append(",WIND_NON_ZERO");
        }
        else
        {
            if (this.stroke instanceof BasicStroke)
                this.commands.append("," + ((BasicStroke) this.stroke).getLineWidth());
            else
                this.commands.append(", 0.1");
        }
        float[] coords = new float[6];
        PathIterator i = path.getPathIterator(null);
        while (!i.isDone())
        {
            int segment = i.currentSegment(coords);
            switch (segment)
            {
                case PathIterator.SEG_CLOSE:
                    this.commands.append(",CLOSE");
                    break;
                case PathIterator.SEG_CUBICTO:
                    this.commands.append(",CUBICTO");
                    addFloatArray(coords, 6);
                    break;
                case PathIterator.SEG_LINETO:
                    this.commands.append(",LINETO");
                    addFloatArray(coords, 2);
                    break;
                case PathIterator.SEG_MOVETO:
                    this.commands.append(",MOVETO");
                    addFloatArray(coords, 2);
                    break;
                case PathIterator.SEG_QUADTO:
                    this.commands.append(",QUADTO");
                    addFloatArray(coords, 4);
                    break;
                default:
                    throw new RuntimeException("unkown segment");
            }
            i.next();
        }
            this.commands.append("</transformPath>\n");
    }

    /**
     * Add a path2D to the command. In case of fill:<br>
     * FILL, transform.m11(h-scale), transform.m12(h-skew), transform.m21(v-skew), transform.m22(v-scale),
     * transform.dx(h-translate), transform.dy(v-translate), fillcolor.r, fillcolor.g, fillcolor.b, fillcolor.alpha,
     * fillcolor.transparency, winding_rule[WIND_EVEN_ODD/WIND_NON_ZERO], COMMAND, coords, COMMAND, coords, ... <br>
     * In case of draw:<br>
     * DRAW, transform.m11(h-scale), transform.m12(h-skew), transform.m21(v-skew), transform.m22(v-scale),
     * transform.dx(h-translate), transform.dy(v-translate), strokecolor.r, strokecolor.g, strokecolor.b, strokecolor.alpha,
     * strokecolor.transparency, line_width, COMMAND, coords, COMMAND, coords, ... <br>
     * where command can be one of the following:<br>
     * - CLOSE, followed by no coordinates<br>
     * - CUBICTO, followed by 3 coordinates (6 numbers)<br>
     * - LINETO, followed by 1 coordinate (2 numbers)<br>
     * - MOVETO, followed by 1 coordinate (2 numbers)<br>
     * - QUADTO, followed by 2 coordinates (4 numbers)<br>
     * @param path Path2D.Double; the path to draw
     * @param fill boolean;
     */
    protected void addTransformPathDouble(Path2D.Double path, boolean fill)
    {
        if (fill)
            this.commands.append("<transformPath>FILL");
        else
            this.commands.append("<transformPath>DRAW");
        addAffineTransform();
        addColor(this.color);
        if (fill)
        {
            if (path.getWindingRule() == Path2D.WIND_EVEN_ODD)
                this.commands.append(",WIND_EVEN_ODD");
            else
                this.commands.append(",WIND_NON_ZERO");
        }
        else
        {
            if (this.stroke instanceof BasicStroke)
                this.commands.append("," + ((BasicStroke) this.stroke).getLineWidth());
            else
                this.commands.append(", 0.1");
        }
        double[] coords = new double[6];
        PathIterator i = path.getPathIterator(null);
        while (!i.isDone())
        {
            int segment = i.currentSegment(coords);
            switch (segment)
            {
                case PathIterator.SEG_CLOSE:
                    this.commands.append(",CLOSE");
                    break;
                case PathIterator.SEG_CUBICTO:
                    this.commands.append(",CUBICTO");
                    addDoubleArray(coords, 6);
                    break;
                case PathIterator.SEG_LINETO:
                    this.commands.append(",LINETO");
                    addDoubleArray(coords, 2);
                    break;
                case PathIterator.SEG_MOVETO:
                    this.commands.append(",MOVETO");
                    addDoubleArray(coords, 2);
                    break;
                case PathIterator.SEG_QUADTO:
                    this.commands.append(",QUADTO");
                    addDoubleArray(coords, 4);
                    break;
                default:
                    throw new RuntimeException("unkown segment");
            }
            i.next();
        }
        this.commands.append("</transformPath>\n");
    }

    /**
     * Add string, 0=command, 1=transform.m11(h-scale), 2=transform.m12(h-skew), 3=transform.m21(v-skew),
     * 4=transform.m22(v-scale), 5=transform.dx(h-translate), 6=transform.dy(v-translate), 7=color.r, 8=color.g, 9=color.b,
     * 10=color.alpha, 11=color.transparency, 12=fontname, 13=fontsize, 14=fontstyle(normal/italic/bold), 15=x, 16=y, 17=text.
     * @param drawCommand String; the tag to use
     * @param params Object...; the params
     */
    protected void addTransformText(String drawCommand, Object... params)
    {
        this.commands.append("<transformText>" + drawCommand);
        addAffineTransform();
        addColor(this.color);
        addFontData();
        for (Object param : params)
        {
            this.commands.append("," + param.toString());
        }
        this.commands.append("</transformText>\n");
    }

    @Override
    public void draw(Shape shape)
    {
        drawFillShape(shape, false);
    }

    /**
     * Draw or fill a shape.
     * @param shape Shape; the shape
     * @param fill boolean; filled or not
     */
    protected void drawFillShape(Shape shape, boolean fill)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.draw(shape: {})", shape.getClass().getSimpleName());
        if (shape instanceof Ellipse2D.Double)
        {
            Ellipse2D.Double ellipse = (Ellipse2D.Double) shape;
            if (fill)
                addTransformFill("fillOval", ellipse.getCenterX(), ellipse.getCenterY(), ellipse.width / 2.0,
                        ellipse.height / 2.0);
            else
                addTransformDraw("drawOval", ellipse.getCenterX(), ellipse.getCenterY(), ellipse.width / 2.0,
                        ellipse.height / 2.0);
        }
        else if (shape instanceof Ellipse2D.Float)
        {
            Ellipse2D.Float ellipse = (Ellipse2D.Float) shape;
            if (fill)
                addTransformFill("fillOval", ellipse.getCenterX(), ellipse.getCenterY(), ellipse.width / 2.0,
                        ellipse.height / 2.0);
            else
                addTransformDraw("drawOval", ellipse.getCenterX(), ellipse.getCenterY(), ellipse.width / 2.0,
                        ellipse.height / 2.0);
        }
        else if (shape instanceof Line2D.Double)
        {
            Line2D.Double line = (Line2D.Double) shape;
            addTransformDraw("drawLine", line.x1, line.y1, line.x2, line.y2);
        }
        else if (shape instanceof Line2D.Float)
        {
            Line2D.Float line = (Line2D.Float) shape;
            addTransformDraw("drawLine", line.x1, line.y1, line.x2, line.y2);
        }
        else if (shape instanceof Rectangle2D.Double)
        {
            Rectangle2D.Double rect = (Rectangle2D.Double) shape;
            if (fill)
                addTransformFill("fillRect", rect.x, rect.y, rect.width, rect.height);
            else
                addTransformDraw("drawRect", rect.x, rect.y, rect.width, rect.height);
        }
        else if (shape instanceof Rectangle2D.Float)
        {
            Rectangle2D.Float rect = (Rectangle2D.Float) shape;
            if (fill)
                addTransformFill("fillRect", rect.x, rect.y, rect.width, rect.height);
            else
                addTransformDraw("drawRect", rect.x, rect.y, rect.width, rect.height);
        }
        else if (shape instanceof Path2D.Float)
        {
            Path2D.Float path = (Path2D.Float) shape;
            addTransformPathFloat(path, fill);
        }
        else if (shape instanceof Path2D.Double)
        {
            Path2D.Double path = (Path2D.Double) shape;
            addTransformPathDouble(path, fill);
        }

    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawImage()");
        return true;
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawImage()");
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawRenderedImage()");
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawRenderableImage()");
    }

    @Override
    public void drawString(String str, int x, int y)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawString()");
        addTransformText("drawString", x, y, str);
    }

    @Override
    public void drawString(String str, float x, float y)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawString()");
        addTransformText("drawString", x, y, str);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawString()");
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawString()");
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawGlyphVector()");
    }

    @Override
    public void fill(Shape shape)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.fill()");
        drawFillShape(shape, true);
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.hit()");
        return false;
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getDeviceConfiguration()");
        return null;
    }

    @Override
    public void setComposite(Composite comp)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setComposite()");
    }

    @Override
    public void setPaint(Paint paint)
    {
        this.paint = paint;
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setPaint()");
    }

    @Override
    public void setStroke(Stroke s)
    {
        this.stroke = s;
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setStroke()");
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue)
    {
        this.renderingHints.put(hintKey, hintValue);
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setRenderingHint()");
    }

    @Override
    public Object getRenderingHint(Key hintKey)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getRenderingHint()");
        return this.renderingHints.get(hintKey);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints)
    {
        this.renderingHints.clear();
        this.renderingHints.putAll(hints);
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setRenderingHints()");
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints)
    {
        this.renderingHints.putAll(hints);
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.addRenderingHints()");
    }

    @Override
    public RenderingHints getRenderingHints()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getRenderingHints()");
        return this.renderingHints;
    }

    @Override
    public void translate(int x, int y)
    {
        this.affineTransform.translate(x, y);
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.translate()");
    }

    @Override
    public void translate(double tx, double ty)
    {
        this.affineTransform.translate(tx, ty);
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.translate()");
    }

    @Override
    public void rotate(double theta)
    {
        this.affineTransform.rotate(theta);
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.rotate()");
    }

    @Override
    public void rotate(double theta, double x, double y)
    {
        this.affineTransform.rotate(theta, x, y);
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.rotate()");
    }

    @Override
    public void scale(double sx, double sy)
    {
        this.affineTransform.scale(sx, sy);
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.scale()");
    }

    @Override
    public void shear(double shx, double shy)
    {
        this.affineTransform.shear(shx, shy);
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.shear()");
    }

    @Override
    public void transform(AffineTransform Tx)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.transform()");
    }

    @Override
    public void setTransform(AffineTransform Tx)
    {
        this.affineTransform = (AffineTransform) Tx.clone();
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setTransform()");
    }

    @Override
    public AffineTransform getTransform()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getTransform()");
        return this.affineTransform;
    }

    @Override
    public Paint getPaint()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getPaint()");
        return this.paint;
    }

    @Override
    public Composite getComposite()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getComposite()");
        return this.composite;
    }

    @Override
    public void setBackground(Color color)
    {
        this.background = color;
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setBackground()");
    }

    @Override
    public Color getBackground()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getBackground()");
        return this.background;
    }

    @Override
    public Stroke getStroke()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getStroke()");
        return this.stroke;
    }

    @Override
    public void clip(Shape s)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.clip()");
    }

    @Override
    public FontRenderContext getFontRenderContext()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getFontRenderContext()");
        return new FontRenderContext(this.affineTransform, true, true);
    }

    @Override
    public Graphics create()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.create()");
        return new HtmlGraphics2D(); // TODO: clone
    }

    @Override
    public Color getColor()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getColor()");
        return this.color;
    }

    @Override
    public void setColor(Color c)
    {
        this.color = c;
        this.paint = c; // TODO see how difference between paint and color should be handled
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setColor()");
    }

    @Override
    public void setPaintMode()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setPaintMode()");
    }

    @Override
    public void setXORMode(Color c1)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setXORMode()");
    }

    @Override
    public Font getFont()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getFont()");
        return this.font;
    }

    @Override
    public void setFont(Font font)
    {
        this.font = font;
        this.fontMetrics = this.canvas.getFontMetrics(this.font);
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setFont()");
    }

    @Override
    public FontMetrics getFontMetrics(Font f)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getFontMetrics()");
        return this.fontMetrics;
    }

    @Override
    public Rectangle getClipBounds()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getClipBounds()");
        return null;
    }

    @Override
    public void clipRect(int x, int y, int width, int height)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.clipRect()");
    }

    @Override
    public void setClip(int x, int y, int width, int height)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setClip()");
    }

    @Override
    public Shape getClip()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.getClip()");
        return null;
    }

    @Override
    public void setClip(Shape clip)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.setClip()");
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.copyArea()");
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawLine()");
        addTransformDraw("drawLine", x1, y1, x2, y2);
    }

    @Override
    public void fillRect(int x, int y, int width, int height)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.fillRect()");
        addTransformFill("fillRect", x, y, width, height);
    }

    @Override
    public void clearRect(int x, int y, int width, int height)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.clearRect()");
        addTransformDraw("clearRect", x, y, width, height);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawRoundRect()");
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.fillRoundRect()");
    }

    @Override
    public void drawOval(int x, int y, int width, int height)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawOval()");
        addTransformDraw("drawOval", x, y, width, height);
    }

    @Override
    public void fillOval(int x, int y, int width, int height)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.fillOval()");
        addTransformFill("fillOval", x, y, width, height);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawArc()");
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.fillArc()");
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.fillPolyline()");
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawPolygon()");
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.fillPolygon()");
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawImage()");
        return false;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawImage()");
        return false;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawImage()");
        return false;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawImage()");
        return false;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
            ImageObserver observer)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawImage()");
        return false;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor,
            ImageObserver observer)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.drawImage()");
        return false;
    }

    @Override
    public void dispose()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphics2D.dispose()");
    }

}
