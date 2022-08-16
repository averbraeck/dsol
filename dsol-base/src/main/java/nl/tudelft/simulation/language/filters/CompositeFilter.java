package nl.tudelft.simulation.language.filters;

/**
 * The composite filter combines two filters.
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
public class CompositeFilter extends AbstractFilter
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /**
     * The enum for the logical operator in composite filters.
     */
    public enum Operator
    {
        /** the AND operator. */
        AND(0),

        /** the OR operator. */
        OR(1);

        /** the value from DSOL-1 before enum was introduced. */
        private final int value;

        /**
         * Create a side; store the value from DSOL-1 as well.
         * @param value int; the value from DSOL-1 before enum was introduced
         */
        Operator(final int value)
        {
            this.value = value;
        }

        /**
         * Returns the value from DSOL-1 before enum was introduced.
         * @return int; the value from DSOL-1 before enum was introduced
         */
        public int getValue()
        {
            return this.value;
        }
    }

    /** the operator of the composite filter. */
    private Operator operator;

    /** the filters to compose. */
    private FilterInterface[] filters = new FilterInterface[2];

    /**
     * constructs a new CompositeFilter.
     * @param filter1 FilterInterface; the first filter
     * @param filter2 FilterInterface; the second filter
     * @param operator Operator; the operator (AND or OR)
     */
    public CompositeFilter(final FilterInterface filter1, final FilterInterface filter2, final Operator operator)
    {
        super();
        if (operator != Operator.AND && operator != Operator.OR)
        {
            throw new IllegalArgumentException("unknown operator");
        }
        this.filters[0] = filter1;
        this.filters[1] = filter2;
        this.operator = operator;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean filter(final Object entry)
    {
        if (this.operator == Operator.AND)
        {
            return this.filters[0].accept(entry) && this.filters[1].accept(entry);
        }
        return this.filters[0].accept(entry) || this.filters[1].accept(entry);
    }

    /**
     * Converts the operator of this filter into a human readable string.
     * @return the operator in human readable string
     */
    protected String operatorToString()
    {
        if (this.operator == Operator.AND)
        {
            return "AND";
        }
        return "OR";
    }

    /** {@inheritDoc} */
    @Override
    public String getCriterion()
    {
        return "composed[" + this.filters[0].getCriterion() + ";" + operatorToString() + ";" + this.filters[1].getCriterion()
                + "]";
    }
}
