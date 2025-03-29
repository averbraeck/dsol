package nl.tudelft.simulation.dsol.web.animation;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.logger.Cat;

/**
 * HtmlDevice.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class HtmlDevice extends GraphicsDevice
{
    /** the GraphicsConfigurations for this HtmlDevice. */
    private GraphicsConfiguration[] htmlGraphicsConfigurations;

    /**
     * @param htmlGraphicsConfiguration the GraphicsConfiguration to add to the HtmlDevice
     */
    public HtmlDevice(GraphicsConfiguration htmlGraphicsConfiguration)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlDevice.<init>");
        this.htmlGraphicsConfigurations = new GraphicsConfiguration[] {htmlGraphicsConfiguration};
    }

    @Override
    public int getType()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlDevice.getType()");
        return GraphicsDevice.TYPE_RASTER_SCREEN;
    }

    @Override
    public String getIDstring()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlDevice.getIDString()");
        return "HtmlDevice";
    }

    @Override
    public GraphicsConfiguration[] getConfigurations()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlDevice.getConfiguration()");
        return this.htmlGraphicsConfigurations;
    }

    @Override
    public GraphicsConfiguration getDefaultConfiguration()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlDevice.getDefaultConfiguration()");
        return this.htmlGraphicsConfigurations[0];
    }

}
