package nl.tudelft.simulation.language.filters;

import java.io.Serializable;

/**
 * The FilterInterface is a general interface for all filters in DSOL. Filters can be based on xY combinations, class
 * information ,etc. etc. The API of implementing filters will explain what it expects as input.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://web.eur.nl/fbk/dep/dep1/Introduction/Staff/People/Lang" >Niels Lang </a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public interface FilterInterface extends Serializable
{
    /**
     * a filter defines whether to accept a value in a chart.
     * @param entry the entry to filter
     * @return whether to accept this entry
     */
    boolean accept(Object entry);

    /**
     * inverts the filter.
     * @param inverted whether to invert the filter
     */
    void setInverted(boolean inverted);

    /**
     * is the filter inverted?
     * @return whether the filter is inverted.
     */
    boolean isInverted();

    /**
     * returns a string representation of the criterion.
     * @return the string representing the criterion
     */
    String getCriterion();

    /**
     * adds filter to this filter and returns the composed filter.
     * @param filter the filter to add
     * @return the composed filter
     */
    FilterInterface and(FilterInterface filter);

    /**
     * creates a new composite filter which is one or two.
     * @param filter the filter to add
     * @return the composed filter
     */
    FilterInterface or(FilterInterface filter);
}
