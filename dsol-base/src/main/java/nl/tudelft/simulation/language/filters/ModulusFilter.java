package nl.tudelft.simulation.language.filters;

/**
 * The modulus filter only accepts the nth event where n % given modulus = 0.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs"> Peter Jacobs </a>
 */
public class ModulusFilter extends AbstractFilter
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** the maxPoints to use. */
    private long modulus = -1;

    /** the amount of points already accepted. */
    private long current = -1;

    /**
     * constructs a new ModulusFilter.
     * @param modulus long; the modulus to use
     */
    public ModulusFilter(final long modulus)
    {
        super();
        this.modulus = modulus;
    }

    /** {@inheritDoc} */
    @Override
    protected synchronized boolean filter(final Object entry)
    {
        this.current++;
        if (this.current % this.modulus == 0)
        {
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getCriterion()
    {
        return "accepts every " + this.modulus + "th entry";
    }
}
