package nl.tudelft.simulation.dsol.swing.gui.util;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.DefaultFormatter;

/**
 * Extension of a DefaultFormatter that uses a regular expression. <br>
 * Derived from <a href="http://www.java2s.com/Tutorial/Java/0240__Swing/RegexFormatterwithaJFormattedTextField.htm">
 * http://www.java2s.com/Tutorial/Java/0240__Swing/RegexFormatterwithaJFormattedTextField.htm</a>
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class RegexFormatter extends DefaultFormatter
{
    /** */
    private static final long serialVersionUID = 20141212L;

    /** The regular expression pattern. */
    private Pattern pattern;

    /**
     * Create a new RegexFormatter.
     * @param pattern String; regular expression pattern that defines what this RexexFormatter will accept
     */
    public RegexFormatter(final String pattern)
    {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public Object stringToValue(final String text) throws ParseException
    {
        Matcher matcher = this.pattern.matcher(text);
        if (matcher.matches())
        {
            return super.stringToValue(text);
        }
        throw new ParseException("Pattern did not match", 0);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "RegexFormatter [pattern=" + this.pattern + "]";
    }
}
