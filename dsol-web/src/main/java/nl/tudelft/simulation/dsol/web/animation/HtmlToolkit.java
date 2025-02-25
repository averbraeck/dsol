package nl.tudelft.simulation.dsol.web.animation;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.logger.Cat;

/**
 * HtmlToolkit.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class HtmlToolkit extends Toolkit
{
    /** the queue of AWT events to process. */
    EventQueue eventQueue = new EventQueue();

    /**
     * 
     */
    public HtmlToolkit()
    {
    }

    @Override
    public Dimension getScreenSize() throws HeadlessException
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.getScreenSize()");
        return null;
    }

    @Override
    public int getScreenResolution() throws HeadlessException
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.getScreenResolution()");
        return 0;
    }

    @Override
    public ColorModel getColorModel() throws HeadlessException
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.getColorModel()");
        return null;
    }

    @Override
    public String[] getFontList()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.getFontList()");
        return null;
    }

    @Override
    public FontMetrics getFontMetrics(Font font)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.getFontMetrics()");
        return null;
    }

    @Override
    public void sync()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.sync()");
    }

    @Override
    public Image getImage(String filename)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.getImage()");
        return null;
    }

    @Override
    public Image getImage(URL url)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.getImage()");
        return null;
    }

    @Override
    public Image createImage(String filename)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.createImage()");
        return null;
    }

    @Override
    public Image createImage(URL url)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.createImage()");
        return null;
    }

    @Override
    public boolean prepareImage(Image image, int width, int height, ImageObserver observer)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.prepareImage()");
        return false;
    }

    @Override
    public int checkImage(Image image, int width, int height, ImageObserver observer)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.checkImage()");
        return 0;
    }

    @Override
    public Image createImage(ImageProducer producer)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.createImage()");
        return null;
    }

    @Override
    public Image createImage(byte[] imagedata, int imageoffset, int imagelength)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.createImage()");
        return null;
    }

    @Override
    public PrintJob getPrintJob(Frame frame, String jobtitle, Properties props)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.getPrintJob()");
        return null;
    }

    @Override
    public void beep()
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.beep()");
    }

    @Override
    public Clipboard getSystemClipboard() throws HeadlessException
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.getSystemClipboard()");
        return null;
    }

    @Override
    protected EventQueue getSystemEventQueueImpl()
    {
        CategoryLogger.filter(Cat.WEB)
                .trace("HtmlToolkit.getSystemEventQueueImpl() -- next event is " + this.eventQueue.peekEvent());
        return this.eventQueue;
    }

    @Override
    public boolean isModalityTypeSupported(ModalityType modalityType)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.isModalityTypeSupported()");
        return false;
    }

    @Override
    public boolean isModalExclusionTypeSupported(ModalExclusionType modalExclusionType)
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.isModalExclusionTypeSupported()");
        return false;
    }

    @Override
    public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight highlight) throws HeadlessException
    {
        CategoryLogger.filter(Cat.WEB).trace("HtmlToolkit.mapInputMethodHighlight()");
        return null;
    }

}
