package nl.tudelft.simulation.dsol.animation.graph;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.djutils.exceptions.Try;
import org.junit.Test;

/**
 * Unit tests for the DoubleAppendList class.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DoubleAppendListTest
{

    /**
     * test the DoubleAppendList.
     */
    @Test
    public void testDoubleAppendList()
    {
        DoubleAppendList list = new DoubleAppendList();
        assertEquals(0, list.size());
        list.add(1.0);
        assertEquals(1, list.size());
        assertEquals(1.0, list.get(0), 0.0001);
        Try.testFail(() -> { list.get(1); }, IndexOutOfBoundsException.class);
        Try.testFail(() -> { list.get(-1); }, IndexOutOfBoundsException.class);

        DoubleAppendList list2 = new DoubleAppendList();
        for (int i = 0; i < 1000; i++)
        {
            list2.add(1.0 * i);
        }
        assertEquals(1000, list2.size());
        for (int i = 0; i < 1000; i++)
        {
            assertEquals(1.0 * i, list2.get(i), 0.001);
        }

        Iterator<Double> iterator = list2.iterator();
        int i = 0;
        while (iterator.hasNext())
        {
            double d = iterator.next();
            assertEquals(1.0 * i, d, 0.001);
            i++;
        }
    }

}
