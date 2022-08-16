package nl.tudelft.simulation.language.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.djutils.exceptions.Try;
import org.junit.Test;

/**
 * Tests the different Filters in dsol-base.
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
public class FilterTest
{
    /**
     * tests the MaxPointFilter.
     */
    @Test
    public void testMaxPointFilter()
    {
        FilterInterface filter = new MaxPointFilter(10);
        for (int i = 0; i < 10; i++)
        {
            assertTrue(filter.accept("entry"));
        }
        assertFalse(filter.accept("entry"));
        assertTrue(filter.getCriterion().contains("accepts the first MaxPoint"));
        assertTrue(filter.getCriterion().contains("10"));
        assertTrue(filter.toString().contains("10"));
        assertTrue(filter.toString().contains("accepts"));
    }

    /**
     * tests the ZeroFilter and the CompositeFilter.
     */
    @Test
    public void testZeroPointCompositeFilter()
    {
        FilterInterface filter = new ZeroFilter();
        assertTrue(filter.accept(null));
        assertTrue(filter.accept("entry"));

        // Let's put the filter on inverted mode
        filter.setInverted(true);
        assertFalse(filter.accept(null));
        assertFalse(filter.accept("entry"));

        // Let's test AND
        filter = filter.and(new ZeroFilter());
        assertFalse(filter.accept("entry"));
        assertTrue(filter.getCriterion().contains("AND"));

        // Let's test OR
        FilterInterface filter1 = new ZeroFilter();
        FilterInterface filter2 = new ZeroFilter();
        assertFalse(filter2.isInverted());
        filter2.setInverted(true);
        assertTrue(filter2.isInverted());
        filter = filter1.or(filter2);
        assertTrue(filter.accept("entry"));
        assertTrue(filter.getCriterion().contains("accepts every entry"));
        assertTrue(filter.getCriterion().contains("OR"));

        assertEquals(0, CompositeFilter.Operator.AND.getValue());
        assertEquals(1, CompositeFilter.Operator.OR.getValue());

        // test illegal and/or
        Try.testFail(() -> { new CompositeFilter(filter1, filter2, null); }, IllegalArgumentException.class);

    }

    /**
     * tests the ModulusFilter.
     */
    @Test
    public void testModulusFilter()
    {
        FilterInterface filter = new ModulusFilter(10);
        for (int i = 0; i < 100; i++)
        {
            if (i % 10 == 0)
            {
                assertTrue(filter.accept("entry"));
            }
            else
            {
                assertFalse(filter.accept("entry"));
            }
        }
        assertTrue(filter.getCriterion().contains("10th entry"));
    }

}
