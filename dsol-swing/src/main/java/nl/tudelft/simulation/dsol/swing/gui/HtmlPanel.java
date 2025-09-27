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
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/mzhang">Mingxin Zhang </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck </a>
 */
public class HtmlPanel extends JEditorPane
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an HTML panel for the user interface.
     */
    public HtmlPanel()
    {
        super();
        super.setEditable(false);
        this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
    }

    /**
     * Construct an HTML panel for the user interface.
     * @param page the URL of the page to display in the panel.
     * @throws IOException in case page cannot be loaded
     */
    public HtmlPanel(final URL page) throws IOException
    {
        this();
        this.setPage(page);
    }

    @Override
    public void setPage(final URL page) throws IOException
    {
        try
        {
            super.setPage(page);
        }
        catch (Exception e)
        {
            CategoryLogger.with(Cat.SWING).warn(e.getMessage());
        }
    }

    /**
     * Method main.
     * @param args arguments for main
     * @throws Exception on error
     */
    public static void main(final String[] args) throws Exception
    {
        String url = "https://simulation.tudelft.nl/dsol/3.05.04/dsol-core/jacoco/index.html";
        if (args.length != 1)
        {
            System.out.println("Usage: java nl.tudelft.simulation.dsol.gui.HtmlPanel [url]");
        }
        else
        {
            url = args[0];
        }
        JFrame app = new JFrame("HtmlPanel, (c) 2003-2025 Delft University of Technology");
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        HtmlPanel htmlPanel = new HtmlPanel(new URL(url));
        app.setContentPane(new JScrollPane(htmlPanel));
        app.setExtendedState(Frame.MAXIMIZED_BOTH);
        app.setVisible(true);
    }
}
