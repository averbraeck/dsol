package nl.tudelft.simulation.dsol.web.animation;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.logger.Cat;

/**
 * The <code>HtmlGraphicsConfiguration</code> class describes the characteristics of the HTML canvas in the browser, as a
 * graphics destination to write to. Note that there can be several <code>GraphicsConfiguration</code> objects associated with a
 * single graphics device, representing different drawing modes or capabilities. <br>
 * <br>
 * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class HtmlGraphicsConfiguration extends GraphicsConfiguration
{
    /** the {@link HtmlDevice} associated with this <code>HtmlGraphicsConfiguration</code>. */
    HtmlDevice htmlDevice;

    /** the identity AffineTransform. */
    AffineTransform identityTransform = new AffineTransform();

    /** the bounds, TODO: which should be filled in some way by the window size in the browser. */
    Rectangle bounds = new Rectangle(0, 0, 1920, 1080);

    /**
     * Create a graphics configuration for the HTML device.
     */
    public HtmlGraphicsConfiguration()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphicsConfiguration.<init>");
    }

    @Override
    public GraphicsDevice getDevice()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphicsConfiguration.getDevice()");
        return this.htmlDevice;
    }

    /**
     * Set the {@link HtmlDevice} associated with this <code>HtmlGraphicsConfiguration</code>.
     * @param htmlDevice HtmlDevice; a &lt;code&gt;GraphicsDevice&lt;/code&gt; object that is associated with this
     *            <code>HtmlGraphicsConfiguration</code>.
     */
    public void setDevice(final HtmlDevice htmlDevice)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphicsConfiguration.setDevice()");
        this.htmlDevice = htmlDevice;
    }

    @Override
    public ColorModel getColorModel()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphicsConfiguration.getColorModel()");
        return ColorModel.getRGBdefault();
    }

    @Override
    public ColorModel getColorModel(int transparency)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphicsConfiguration.getColorModel()");
        return ColorModel.getRGBdefault();
    }

    @Override
    public AffineTransform getDefaultTransform()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphicsConfiguration.getDefaultTransform()");
        return this.identityTransform;
    }

    @Override
    public AffineTransform getNormalizingTransform()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphicsConfiguration.getNormalizingTransform()");
        return this.identityTransform;
    }

    @Override
    public Rectangle getBounds()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlGraphicsConfiguration.getBounds()");
        return this.bounds;
    }

}
