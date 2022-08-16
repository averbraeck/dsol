package nl.tudelft.simulation.jstats.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import nl.tudelft.simulation.language.filters.FilterInterface;

/**
 * Tests the MaxDiffFilter.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://web.eur.nl/fbk/dep/dep1/Introduction/Staff/People/Lang">Niels Lang
 *         </a><a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class MaxDiffFilterTest
{
    /**
     * tests the ZeroFilter.
     */
    public void testMaxDiffFilter()
    {
        FilterInterface filter = new MaxDiffFilter(0.25);
        assertTrue(filter.accept(new double[] {0.0, 10}));
        assertFalse(filter.accept(new double[] {0.0, 12.4}));
        assertFalse(filter.accept(new double[] {0.0, 10}));
        assertTrue(filter.accept(new double[] {0.0, 12.6}));
    }
}
