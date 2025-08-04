package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Console for a swing application where the standard output and standard error output are displayed.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class ConsoleOutput extends JPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The text area for the output. */
    private JTextPane textPane;

    /** The scroll pane in which the text is shown. */
    private final JScrollPane scrollPane;

    /** Reference to the old stdout. */
    private PrintStream standardOut;

    /** Reference to the old stderr. */
    private PrintStream standardErr;

    /** the maximum number of lines. */
    private int maxLines = 1000;

    /** the font to use. */
    private Font font;

    /**
     * Constructor for Console.
     */
    public ConsoleOutput()
    {
        setLayout(new BorderLayout());
        this.textPane = new JTextPane();
        this.font = new Font("monospaced", Font.PLAIN, 12);
        this.textPane.setEditable(false);
        this.textPane.setOpaque(false);

        this.scrollPane = new JScrollPane(this.textPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(this.scrollPane, BorderLayout.CENTER);

        // save originals
        this.standardOut = System.out;
        this.standardErr = System.err;

        // redirect two print streams to the console
        System.setOut(new PrintStream(new BufferedConsoleStream(this, this.textPane, this.standardOut, false)));
        System.setErr(new PrintStream(new BufferedConsoleStream(this, this.textPane, this.standardErr, true)));
    }

    /**
     * Set another font for the console.
     * @param font the new font
     */
    public void setConsoleFont(final Font font)
    {
        this.font = font;
    }

    /**
     * Return font for the console.
     * @return the font for the console
     */
    public Font getConsoleFont()
    {
        return this.font;
    }

    /**
     * Set the maximum number of lines.
     * @param maxLines the new maximum number of lines
     */
    public void setMaxLines(final int maxLines)
    {
        this.maxLines = maxLines;
    }

    /**
     * Return the maximum number of lines.
     * @return the maximum number of lines.
     */
    public int getMaxLines()
    {
        return this.maxLines;
    }

    /** The custom output stream that writes to the text pane. */
    public static class BufferedConsoleStream extends OutputStream
    {
        /** the text area to write to. */
        private final JTextPane textPane;

        /** the original stdout/stderr. */
        private final PrintStream original;

        /** stderr or stdout? */
        private final boolean isError;

        /** buffer limit. */
        private static final int BUFFER_LIMIT = 8192;

        /** the buffer for buffered writing. */
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        /** the document to write to. */
        private final StyledDocument doc;

        /** the style for stdout. */
        private final Style styleNormal;

        /** the style for stderr. */
        private final Style styleError;

        /** pointer to the console. */
        private final ConsoleOutput consoleOutput;

        /**
         * Create the custom writer.
         * @param consoleOutput the console output class
         * @param textPane the text area to write to
         * @param original the original stdout/stderr
         * @param isError stderr or stdout
         */
        public BufferedConsoleStream(final ConsoleOutput consoleOutput, final JTextPane textPane, final PrintStream original,
                final boolean isError)
        {
            this.textPane = textPane;
            this.original = original;
            this.isError = isError;
            this.consoleOutput = consoleOutput;

            this.doc = this.textPane.getStyledDocument();

            // Create text styles
            this.styleNormal = this.textPane.addStyle("Normal", null);
            StyleConstants.setForeground(this.styleNormal, Color.BLACK);

            this.styleError = this.textPane.addStyle("Error", null);
            StyleConstants.setForeground(this.styleError, Color.RED);
        }

        @Override
        public void write(final int b) throws IOException
        {
            this.buffer.write(b);
            if (b == '\n' || this.buffer.size() >= BUFFER_LIMIT)
            {
                flush();
            }
        }

        @Override
        public void flush()
        {
            String text = this.buffer.toString();
            this.buffer.reset();

            this.original.print(text); // Pass to original output

            SwingUtilities.invokeLater(() ->
            {
                try
                {
                    JScrollBar vScroll = ((JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, this.textPane))
                            .getVerticalScrollBar();
                    boolean atBottom = (vScroll.getValue() + vScroll.getVisibleAmount()) == vScroll.getMaximum();

                    this.textPane.setFont(this.consoleOutput.getConsoleFont());
                    Style style = this.isError ? this.styleError : this.styleNormal;
                    int startOffset = this.doc.getLength();
                    this.doc.insertString(startOffset, text, style);

                    trimLines();

                    if (atBottom)
                    {
                        this.textPane.setCaretPosition(this.doc.getLength());
                    }

                }
                catch (BadLocationException ignored)
                {
                }
            });
        }

        /**
         * 
         */
        private void trimLines()
        {
            Element root = this.doc.getDefaultRootElement();
            int lineCount = root.getElementCount();
            if (lineCount <= this.consoleOutput.getMaxLines())
                return;

            try
            {
                int linesToRemove = lineCount - this.consoleOutput.getMaxLines();
                int endOffset = root.getElement(linesToRemove - 1).getEndOffset();
                this.doc.remove(0, endOffset);
            }
            catch (BadLocationException ignored)
            {
            }
        }

    }

}
