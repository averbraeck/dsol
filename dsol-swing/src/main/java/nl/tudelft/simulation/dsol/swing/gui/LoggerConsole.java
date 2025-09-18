package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.UUID;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import org.djutils.logger.CategoryLogger;
import org.djutils.logger.CategoryLogger.CategoryAppenderFactory;
import org.djutils.logger.LogCategory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControl;

/**
 * Console for a swing application where the log messages are displayed.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class LoggerConsole extends JPanel implements AppearanceControl
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the document to write to. */
    private StyledDocument doc;

    /** the color style. */
    private Style style;

    /** number of lines. */
    private int nrLines = 0;

    /** the maximum number of lines before the first lines will be erased. */
    private int maxLines = 20000;

    /** the text pane. */
    private JTextPane textPane;

    /** the appender factory with its id. */
    JConsoleAppender appender;

    /**
     * Constructor for LoggerConsole.
     */
    {
        setLayout(new BorderLayout());
        this.textPane = new JTextPane();
        this.textPane.setEditable(false);
        this.textPane.setBackground(Color.WHITE);
        this.textPane.setOpaque(true);
        var appenderFactory = new JConsoleAppenderFactory(this);
        String id = UUID.randomUUID().toString();
        CategoryLogger.addAppender(id, appenderFactory);
        JScrollPane scrollPane = new JScrollPane(this.textPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setOpaque(true);
        add(scrollPane, BorderLayout.CENTER);
        this.doc = this.textPane.getStyledDocument();
        this.style = this.textPane.addStyle("colorStyle", null);
    }

    /**
     * Write one or more log lines to the document. Note that the log line can be split into multiple lines.
     * @param logLines the lines to write to the document
     */
    protected void addLine(final String logLines)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                String[] lines = logLines.split("\\r?\\n");

                while (LoggerConsole.this.nrLines > Math.max(0, LoggerConsole.this.maxLines - lines.length))
                {
                    Document document = LoggerConsole.this.doc;
                    Element root = document.getDefaultRootElement();
                    Element line = root.getElement(0);
                    int end = line.getEndOffset();

                    try
                    {
                        document.remove(0, end);
                        LoggerConsole.this.nrLines--;
                    }
                    catch (BadLocationException exception)
                    {
                        // we cannot log this -- that would generate an infinite loop
                        System.err.println("Was not able to remove lines from the document");
                        break;
                    }
                }
                try
                {
                    for (String line : lines)
                    {
                        LoggerConsole.this.doc.insertString(LoggerConsole.this.doc.getLength(), line + "\n",
                                LoggerConsole.this.style);
                        LoggerConsole.this.nrLines++;
                    }
                }
                catch (Exception exception)
                {
                    // we cannot log this -- that would generate an infinite loop
                    System.err.println("Was not able to insert text in the Console");
                }
                LoggerConsole.this.textPane.setCaretPosition(LoggerConsole.this.doc.getLength());
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Set the maximum number of lines in the console before the first lines will be erased. The number of lines should be at
     * least 1. If the provided number of lines is less than 1, it wil be set to 1.
     * @param maxLines set the maximum number of lines before the first lines will be erased
     */
    public void setMaxLines(final int maxLines)
    {
        this.maxLines = Math.max(1, maxLines);
    }

    @Override
    public boolean isBackground()
    {
        return true;
    }

    /**
     * The in-memory StringAppender class for testing whether the correct information has been logged.
     */
    protected static class JConsoleAppender extends AppenderBase<ILoggingEvent>
    {
        /** the last used pattern. */
        private final String pattern;

        /** The logger context. */
        private final LoggerContext ctx;

        /** The LoggerConsole for this factory. */
        private final LoggerConsole loggerConsole;

        /**
         * Create a new Appender.
         * @param loggerConsole the LoggerConsole for this appender for the addLine() method
         * @param pattern the formatting pattern
         * @param ctx the logger context
         */
        public JConsoleAppender(final LoggerConsole loggerConsole, final String pattern, final LoggerContext ctx)
        {
            this.loggerConsole = loggerConsole;
            this.pattern = pattern;
            this.ctx = ctx;
        }

        @Override
        protected void append(final ILoggingEvent event)
        {
            PatternLayout layout = new PatternLayout();
            layout.setContext(this.ctx);
            layout.setPattern(this.pattern);
            layout.start();
            String logLine = layout.doLayout(event);
            this.loggerConsole.addLine(logLine);
        }
    }

    /**
     * The Factory return an appender that can log messages to the console.
     */
    protected static class JConsoleAppenderFactory implements CategoryAppenderFactory
    {
        /** The LoggerConsole for this factory. */
        private final LoggerConsole loggerConsole;

        /**
         * Instantiate the AppenderFactory.
         * @param loggerConsole the LoggerConsole for this factory
         */
        public JConsoleAppenderFactory(final LoggerConsole loggerConsole)
        {
            this.loggerConsole = loggerConsole;
        }

        @Override
        public Appender<ILoggingEvent> create(final String id, final LogCategory category, final String pattern,
                final LoggerContext ctx)
        {
            JConsoleAppender app = new JConsoleAppender(this.loggerConsole, pattern, ctx);
            app.setContext(ctx);
            app.setName(id + "@" + category.toString());
            return app;
        }

        @Override
        public String id()
        {
            return "jconsole";
        }
    }

}
