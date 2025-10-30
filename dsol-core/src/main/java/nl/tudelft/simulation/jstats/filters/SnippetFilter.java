package nl.tudelft.simulation.jstats.filters;

import nl.tudelft.simulation.language.filters.AbstractFilter;

/**
 * The snippet filter only accepts one entry per snippet value. A snippet is a range in x-value.
 * <p>
 * Copyright (c) 2004-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class SnippetFilter extends AbstractFilter
{
    /** the snippet representing the xRange for this filter. */
    private double snippet = Double.NaN;

    /** the amount of points already accepted. */
    private double lastAcceptedXValue = -Double.MAX_VALUE;

    /**
     * constructs a new SnippetFilter. A snippet filter test only accepts one entry per snippet value. A snippet is a range in
     * x-value.
     * @param snippet the snippet to use. A snippet is a range in the x-value.
     */
    public SnippetFilter(final double snippet)
    {
        super();
        if (snippet <= 0.0)
        {
            throw new IllegalArgumentException("snippet should be >0.0");
        }
        this.snippet = snippet;
    }

    @Override
    public boolean filter(final Object entry)
    {
        if (!(entry instanceof double[]) || ((double[]) entry).length != 2)
        {
            throw new IllegalArgumentException("entry should be instance of double[2] representing x,y");
        }
        double[] value = (double[]) entry;
        if ((value[0] - this.lastAcceptedXValue) >= this.snippet)
        {
            this.lastAcceptedXValue = value[0];
            return true;
        }
        return false;
    }

    @Override
    public String getCriterion()
    {
        return "accepts one entry per " + this.snippet + " xRange value";
    }
}
