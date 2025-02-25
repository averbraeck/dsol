package nl.tudelft.simulation.jstats.filters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.language.filters.FilterInterface;

/**
 * Tests the MaxDiffFilter.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://web.eur.nl/fbk/dep/dep1/Introduction/Staff/People/Lang">Niels Lang
 *         </a><a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class SnippetFilterTest
{
    /**
     * tests the SnippetFilter.
     */
    @Test
    public void testSnippetFilter()
    {
        FilterInterface filter = new SnippetFilter(1.0);
        assertTrue(filter.accept(new double[] {10.0, 0}));
        assertFalse(filter.accept(new double[] {10.1, 0.0}));
        assertTrue(filter.accept(new double[] {11.0, 0.0}));
        assertFalse(filter.accept(new double[] {11.1, 0.0}));
        assertFalse(filter.accept(new double[] {11.5, 0.0}));
    }
}
