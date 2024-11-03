package nl.tudelft.simulation.language.filters;

/**
 * The histogram specifies a histogram chart for the DSOL framework.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs"> Peter Jacobs </a>
 */
public class MaxPointFilter extends AbstractFilter
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** the maxPoints to use. */
    private long maxPoints = -1;

    /** the amount of points already accepted. */
    private long accepted = 0;

    /**
     * constructs a new MaxPointFilter.
     * @param maxPoints long; the maximum points to display
     */
    public MaxPointFilter(final long maxPoints)
    {
        super();
        this.maxPoints = maxPoints;
    }

    @Override
    protected synchronized boolean filter(final Object entry)
    {
        this.accepted++;
        if (this.accepted > this.maxPoints)
        {
            return false;
        }
        return true;
    }

    @Override
    public String getCriterion()
    {
        return "accepts the first MaxPoint(=" + this.maxPoints + ") entries";
    }
}
