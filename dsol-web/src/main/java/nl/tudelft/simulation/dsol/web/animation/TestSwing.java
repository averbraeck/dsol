package nl.tudelft.simulation.dsol.web.animation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * TestSwing.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class TestSwing
{
    /** */
    public TestSwing()
    {
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setMinimumSize(new Dimension(200, 200));
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label, BorderLayout.NORTH);
        frame.getContentPane().add(new CustomPanel(), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @param args String[]; empty
     */
    public static void main(String[] args)
    {
        System.setProperty("java.awt.graphicsenv", "nl.tudelft.simulation.dsol.web.animation.HTMLGraphicsEnvironment");
        System.setProperty("awt.toolkit", "nl.tudelft.simulation.dsol.web.animation.HTMLToolkit");
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new TestSwing();
            }
        });
    }

    /** */
    protected class CustomPanel extends JPanel
    {
        /** */
        public CustomPanel()
        {
            setMinimumSize(new Dimension(100, 100));
        }

        /** {@inheritDoc} */
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.fillOval(50, 10, 60, 60);
        }

    }
}
