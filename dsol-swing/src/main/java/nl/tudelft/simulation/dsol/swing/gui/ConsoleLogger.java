package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.EnumSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.LogEntry;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.Writer;

import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControl;

/**
 * Console for a swing application where the log messages are displayed.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class ConsoleLogger extends JPanel implements AppearanceControl
{
    /** */
    private static final long serialVersionUID = 1L;

    /** */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected ConsoleLogWriter consoleLogWriter;

    /** current message format. */
    private String messageFormat = CategoryLogger.DEFAULT_MESSAGE_FORMAT;

    /** the current logging level. */
    private Level level = Level.INFO;
    
    /** the text pane. */
    private JTextPane textPane;

    /**
     * Constructor for Logger Console.
     * @param logLevel Level the logLevel to use;
     */
    public ConsoleLogger(final Level logLevel)
    {
        this.level = logLevel;
        setLayout(new BorderLayout());
        this.textPane = new JTextPane();
        this.textPane.setEditable(false);
        this.textPane.setBackground(Color.WHITE);
        this.textPane.setOpaque(true);
        this.consoleLogWriter = new ConsoleLogWriter(this.textPane);
        Configurator.currentConfig().addWriter(this.consoleLogWriter, this.level, this.messageFormat).activate();
        JScrollPane scrollPane = new JScrollPane(this.textPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setOpaque(true);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Set a new logging format for the message lines of the Console writer. The default message format is:<br>
     * {class_name}.{method}:{line} {message|indent=4}<br>
     * <br>
     * A few popular placeholders that can be used:<br>
     * - {class} Fully-qualified class name where the logging request is issued<br>
     * - {class_name} Class name (without package) where the logging request is issued<br>
     * - {date} Date and time of the logging request, e.g. {date:yyyy-MM-dd HH:mm:ss} [SimpleDateFormat]<br>
     * - {level} Logging level of the created log entry<br>
     * - {line} Line number from where the logging request is issued<br>
     * - {message} Associated message of the created log entry<br>
     * - {method} Method name from where the logging request is issued<br>
     * - {package} Package where the logging request is issued<br>
     * @see <a href="https://tinylog.org/configuration#format">https://tinylog.org/configuration</a>
     * @param newMessageFormat String; the new formatting pattern to use
     */
    public void setLogMessageFormat(final String newMessageFormat)
    {
        Configurator.currentConfig().removeWriter(this.consoleLogWriter).activate();
        this.messageFormat = newMessageFormat;
        Configurator.currentConfig().addWriter(this.consoleLogWriter, this.level, this.messageFormat).activate();
    }

    /**
     * @param newLevel Level; the new log level for the Console
     */
    public void setLogLevel(final Level newLevel)
    {
        Configurator.currentConfig().removeWriter(this.consoleLogWriter).activate();
        this.level = newLevel;
        Configurator.currentConfig().addWriter(this.consoleLogWriter, this.level, this.messageFormat).activate();
    }

    /**
     * Set the maximum number of lines in the console before the first lines will be erased. The number of lines should be at
     * least 1. If the provided number of lines is less than 1, it wil be set to 1.
     * @param maxLines int; set the maximum number of lines before the first lines will be erased
     */
    public void setMaxLines(final int maxLines)
    {
        this.consoleLogWriter.maxLines = Math.max(1, maxLines);
    }

    
    /** {@inheritDoc} */
    @Override
    public boolean isBackground()
    {
        return true;
    }

    /**
     * LogWriter takes care of writing the log records to the console. <br>
     * <br>
     * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://www.simulation.tudelft.nl/" target="_blank"> www.simulation.tudelft.nl</a>.
     * The source code and binary code of this software is proprietary information of Delft University of Technology.
     * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
     */
    public static class ConsoleLogWriter implements Writer
    {
        /** the text pane. */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        JTextPane textPane;

        /** the document to write to. */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        StyledDocument doc;

        /** the color style. */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        Style style;

        /** number of lines. */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        int nrLines = 0;

        /** the maximum number of lines before the first lines will be erased. */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        protected int maxLines = 20000;

        /**
         * @param textPane JTextPane; the text area to write the messages to.
         */
        public ConsoleLogWriter(final JTextPane textPane)
        {
            this.textPane = textPane;
            this.doc = textPane.getStyledDocument();
            this.style = textPane.addStyle("colorStyle", null);
        }

        /** {@inheritDoc} */
        @Override
        public Set<LogEntryValue> getRequiredLogEntryValues()
        {
            return EnumSet.of(LogEntryValue.RENDERED_LOG_ENTRY); // Only the final rendered log entry is required
        }

        /** {@inheritDoc} */
        @Override
        public void init(final Configuration configuration) throws Exception
        {
            // nothing to do
        }

        /** {@inheritDoc} */
        @Override
        public synchronized void write(final LogEntry logEntry) throws Exception
        {
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    String[] lines = logEntry.getRenderedLogEntry().split("\\r?\\n");

                    while (ConsoleLogWriter.this.nrLines > Math.max(0, ConsoleLogWriter.this.maxLines - lines.length))
                    {
                        Document document = ConsoleLogWriter.this.doc;
                        Element root = document.getDefaultRootElement();
                        Element line = root.getElement(0);
                        int end = line.getEndOffset();

                        try
                        {
                            document.remove(0, end);
                            ConsoleLogWriter.this.nrLines--;
                        }
                        catch (BadLocationException exception)
                        {
                            CategoryLogger.always().error(exception);
                            break;
                        }
                    }
                    switch (logEntry.getLevel())
                    {
                        case TRACE:
                            StyleConstants.setForeground(ConsoleLogWriter.this.style, Color.DARK_GRAY);
                            break;

                        case DEBUG:
                            StyleConstants.setForeground(ConsoleLogWriter.this.style, Color.BLUE);
                            break;

                        case INFO:
                            StyleConstants.setForeground(ConsoleLogWriter.this.style, Color.BLACK);
                            break;

                        case WARNING:
                            StyleConstants.setForeground(ConsoleLogWriter.this.style, Color.MAGENTA);
                            break;

                        case ERROR:
                            StyleConstants.setForeground(ConsoleLogWriter.this.style, Color.RED);
                            break;

                        default:
                            break;
                    }
                    try
                    {
                        for (String line : lines)
                        {
                            ConsoleLogWriter.this.doc.insertString(ConsoleLogWriter.this.doc.getLength(), line + "\n",
                                    ConsoleLogWriter.this.style);
                            ConsoleLogWriter.this.nrLines++;
                        }
                    }
                    catch (Exception exception)
                    {
                        // we cannot log this -- that would generate an infinite loop
                        System.err.println("Was not able to insert text in the Console");
                    }
                    ConsoleLogWriter.this.textPane.setCaretPosition(ConsoleLogWriter.this.doc.getLength());
                }
            };
            SwingUtilities.invokeLater(runnable);
        }

        /** {@inheritDoc} */
        @Override
        public void flush() throws Exception
        {
            // nothing to do
        }

        /** {@inheritDoc} */
        @Override
        public void close() throws Exception
        {
            // nothing to do
        }

    }
}
