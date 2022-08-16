package nl.tudelft.simulation.jstats.streams;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

/**
 * The DistributionsGUIInspector provides graphical insight in the randomness of different streams.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class StreamsGuiInspector extends JPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * the stream of the frame.
     */
    private StreamInterface stream = null;

    /**
     * constructs a new DistributionsGUIInspector.
     * @param stream the stream to display
     */
    public StreamsGuiInspector(final StreamInterface stream)
    {
        this.setPreferredSize(new Dimension(500, 500));
        this.stream = stream;
    }

    /** {@inheritDoc} */
    @Override
    protected void paintComponent(final Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < 10000; i++)
        {
            g2.fillOval(this.stream.nextInt(0, 500), this.stream.nextInt(0, 500), 1, 1);
        }
    }

    /**
     * executes the main program.
     * @param args the commandline arguments
     */
    public static void main(final String[] args)
    {
        JFrame app = new JFrame("Stream gui tester");
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTabbedPane contentPane = new JTabbedPane();
        contentPane.add("Java2Random", new StreamsGuiInspector(new Java2Random()));
        contentPane.add("MersenneTwister", new StreamsGuiInspector(new MersenneTwister()));
        contentPane.add("DX120Generator", new StreamsGuiInspector(new DX120Generator()));

        app.getContentPane().add(contentPane);
        app.pack();
        app.setVisible(true);
    }
}
