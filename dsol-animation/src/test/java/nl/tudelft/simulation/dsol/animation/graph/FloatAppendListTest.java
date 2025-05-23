package nl.tudelft.simulation.dsol.animation.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the FloatAppendList class.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class FloatAppendListTest
{

    /**
     * test the FloatAppendList.
     */
    @Test
    public void testFloatAppendList()
    {
        FloatAppendList list = new FloatAppendList();
        assertEquals(0, list.size());
        list.add(1.0f);
        assertEquals(1, list.size());
        assertEquals(1.0, list.get(0), 0.0001);
        Try.testFail(() -> {
            list.get(1);
        }, IndexOutOfBoundsException.class);
        Try.testFail(() -> {
            list.get(-1);
        }, IndexOutOfBoundsException.class);

        FloatAppendList list2 = new FloatAppendList();
        for (int i = 0; i < 1000; i++)
        {
            list2.add(1.0f * i);
        }
        assertEquals(1000, list2.size());
        for (int i = 0; i < 1000; i++)
        {
            assertEquals(1.0 * i, list2.get(i), 0.001);
        }

        Iterator<Float> iterator = list2.iterator();
        int i = 0;
        while (iterator.hasNext())
        {
            double d = iterator.next();
            assertEquals(1.0 * i, d, 0.001);
            i++;
        }
    }

}
