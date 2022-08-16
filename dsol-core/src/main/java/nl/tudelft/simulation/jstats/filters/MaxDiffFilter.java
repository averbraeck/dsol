package nl.tudelft.simulation.jstats.filters;

import nl.tudelft.simulation.language.filters.AbstractFilter;

/**
 * The MaxDiffFilter accepts entries if their value is larger than the percentage of the last received Value.
 * <p>
 * Copyright (c) 2004-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MaxDiffFilter extends AbstractFilter
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the snippet representing the xRange for this filter. */
    private double acceptedDifferencePercentage = Double.NaN;

    /** the amount of points already accepted. */
    private double lastReceivedValue = -Double.MAX_VALUE;

    /**
     * constructs a new MaxDiffFilter.
     * @param acceptedDifferencePercentage double; the maximum accepted difference percentage, e.g. 10%
     */
    public MaxDiffFilter(final double acceptedDifferencePercentage)
    {
        super();
        if (acceptedDifferencePercentage <= 0 && acceptedDifferencePercentage > 1.0)
        {
            throw new IllegalArgumentException("percentage should be between [0.0,1.0]");
        }
        this.acceptedDifferencePercentage = acceptedDifferencePercentage;
    }

    /**
     * filters based on the maximum difference.
     * @param entry Object; we expect a double[2] representing x,y as input.
     * @see nl.tudelft.simulation.language.filters.AbstractFilter#filter(java.lang.Object)
     * @return whether to accept the entry
     */
    @Override
    public boolean filter(final Object entry)
    {
        if (!(entry instanceof double[]) || ((double[]) entry).length != 2)
        {
            throw new IllegalArgumentException("entry should be instance of double[2] representing x,y");
        }
        double[] value = (double[]) entry;
        if ((Math.abs(value[1] - this.lastReceivedValue)) >= this.lastReceivedValue * this.acceptedDifferencePercentage)
        {
            this.lastReceivedValue = value[1];
            return true;
        }
        this.lastReceivedValue = value[1];
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getCriterion()
    {
        return "accepts entries if their value>" + this.acceptedDifferencePercentage + "% of the last received Value";
    }
}
