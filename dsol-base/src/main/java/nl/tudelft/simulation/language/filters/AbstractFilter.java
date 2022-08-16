package nl.tudelft.simulation.language.filters;

/**
 * The abstract filter forms the abstract class for all filters. The filter method should be implemented by all subclasses. This
 * filter method should have the same semantics as the accept(inverted=false) method.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://web.eur.nl/fbk/dep/dep1/Introduction/Staff/People/Lang" >Niels Lang </a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public abstract class AbstractFilter implements FilterInterface
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** is this filter inverted. */
    protected boolean inverted = false;

    /**
     * constructs a new AbstractFilter.
     */
    public AbstractFilter()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInverted()
    {
        return this.inverted;
    }

    /** {@inheritDoc} */
    @Override
    public void setInverted(final boolean inverted)
    {
        this.inverted = inverted;
    }

    /** {@inheritDoc} */
    @Override
    public boolean accept(final Object entry)
    {
        boolean value = this.filter(entry);
        if (!this.inverted)
        {
            return value;
        }
        return !value;
    }

    /**
     * filters the entry. This method should be implemented by every filter based on its semantic meaning.
     * @param entry Object; the entry to filter.
     * @return whether to accept the value.
     */
    protected abstract boolean filter(Object entry);

    /** {@inheritDoc} */
    @Override
    public abstract String getCriterion();

    /** {@inheritDoc} */
    @Override
    public FilterInterface and(final FilterInterface filter)
    {
        return new CompositeFilter(this, filter, CompositeFilter.Operator.AND);
    }

    /** {@inheritDoc} */
    @Override
    public FilterInterface or(final FilterInterface filter)
    {
        return new CompositeFilter(this, filter, CompositeFilter.Operator.OR);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Filter[criterion=" + this.getCriterion() + ";inverted=" + this.inverted + "]";
    }

}
