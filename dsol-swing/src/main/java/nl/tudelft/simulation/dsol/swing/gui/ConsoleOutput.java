package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * Console for a swing application where the standard output and standard error output are displayed.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class ConsoleOutput extends JPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The text area for the output. */
    private JTextArea textArea;
    
    /** Reference to the old stdout. */
    private PrintStream standardOut;

    /** Reference to the old stderr. */
    private PrintStream standardErr;

    /**
     * Constructor for Console.
     */
    public ConsoleOutput()
    {
        setLayout(new BorderLayout());
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.standardOut = System.out;
        this.standardErr = System.err;
        System.setOut(new PrintStream(new CustomOutputStream(this.textArea, this.standardOut)));
        System.setErr(new PrintStream(new CustomErrorStream(this.textArea, this.standardErr)));
        JScrollPane scrollPane = new JScrollPane(this.textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    /** The custom output stream that writes to the text pane. */
    public static class CustomOutputStream extends OutputStream
    {
        /** the text area to write to. */
        private final JTextArea textArea;
        
        /** the original stdout. */
        private final PrintStream standardOut;

        /**
         * Create the custom writer.
         * @param textArea JTextArea; the text area to write to
         * @param standardOut  PrintSTream; the original stdout
         */
        public CustomOutputStream(final JTextArea textArea, final PrintStream standardOut)
        {
            this.textArea = textArea;
            this.standardOut = standardOut;
        }

        /** {@inheritDoc} */
        @Override
        public void write(final int b) throws IOException
        {
            this.textArea.append(String.valueOf((char) b));
            this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
            this.standardOut.append(String.valueOf((char) b));
        }
    }
    
    /** The custom error stream that writes to the text pane. */
    public static class CustomErrorStream extends OutputStream
    {
        /** the text area to write to. */
        private final JTextArea textArea;
        
        /** the original stderr. */
        private final PrintStream standardErr;

        /**
         * Create the custom writer.
         * @param textArea JTextArea; the text area to write to
         * @param standardErr PrintSTream; the original stderr
         */
        public CustomErrorStream(final JTextArea textArea, final PrintStream standardErr)
        {
            this.textArea = textArea;
            this.standardErr = standardErr;
        }

        /** {@inheritDoc} */
        @Override
        public void write(final int b) throws IOException
        {
            this.textArea.append(String.valueOf((char) b));
            this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
            this.standardErr.append(String.valueOf((char) b));
        }
    }

}
