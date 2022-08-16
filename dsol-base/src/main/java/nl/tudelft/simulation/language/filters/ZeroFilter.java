package nl.tudelft.simulation.language.filters;

/**
 * The Zero filter does not filter any value.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs"> Peter Jacobs </a>
 */
public class ZeroFilter extends AbstractFilter
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new ZeroFilte.
     */
    public ZeroFilter()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    protected boolean filter(final Object entry)
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String getCriterion()
    {
        return "accepts every entry";
    }
}
