package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.Frame;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.logger.Cat;

/**
 * A simple Swing component to show HTML content from a URL. 
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/mzhang">Mingxin Zhang </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck </a>
 */
public class HTMLPanel extends JEditorPane
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an HTML panel for the user interface.
     */
    public HTMLPanel()
    {
        super();
        super.setEditable(false);
        this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
    }

    /**
     * Construct an HTML panel for the user interface.
     * @param page URL; the URL of the page to display in the panel.
     * @throws IOException in case page cannot be loaded
     */
    public HTMLPanel(final URL page) throws IOException
    {
        this();
        this.setPage(page);
    }

    /** {@inheritDoc} */
    @Override
    public void setPage(final URL page) throws IOException
    {
        try
        {
            super.setPage(page);
        }
        catch (Exception e)
        {
            CategoryLogger.filter(Cat.SWING).warn(e.getMessage());
        }
    }

    /**
     * Method main.
     * @param args String[]; arguments for main
     * @throws Exception on error
     */
    public static void main(final String[] args) throws Exception
    {
        String url = "https://simulation.tudelft.nl/dsol/3.05.04/dsol-core/jacoco/index.html";
        if (args.length != 1)
        {
            System.out.println("Usage: java nl.tudelft.simulation.dsol.gui.HTMLPanel [url]");
        }
        else
        {
            url = args[0];
        }
        JFrame app = new JFrame("HTMLPanel, (c) 2003-2022 Delft University of Technology");
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        HTMLPanel htmlPanel = new HTMLPanel(new URL(url));
        app.setContentPane(new JScrollPane(htmlPanel));
        app.setExtendedState(Frame.MAXIMIZED_BOTH);
        app.setVisible(true);
    }
}
