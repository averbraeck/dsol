package nl.tudelft.simulation.dsol.simtime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.junit.jupiter.api.Test;

/**
 * SimTimeTest tests the SimTime classes.
 * <p>
 * Copyright (c) 2021-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SimTimeTest
{

    /**
     * test plus.
     */
    @Test
    public void testPlus()
    {
        assertEquals(Double.valueOf(8.0), SimTime.plus(Double.valueOf(5.0), Double.valueOf(3.0)), 1E-6);
        assertEquals(Float.valueOf(8.0f), SimTime.plus(Float.valueOf(5.0f), Float.valueOf(3.0f)), 1E-6);
        assertEquals(Long.valueOf(8L), SimTime.plus(Long.valueOf(5L), Long.valueOf(3L)));
        assertEquals(8.0, SimTime.plus(5.0, 3.0), 1E-6);
        assertEquals(8.0f, SimTime.plus(5.0f, 3.0f), 1E-6);
        assertEquals(Long.valueOf(8L), SimTime.plus(5L, 3L));
        assertEquals(Duration.instantiateSI(8.0), SimTime.plus(Duration.instantiateSI(5.0), Duration.instantiateSI(3.0)));
        assertEquals(FloatDuration.instantiateSI(8.0f),
                SimTime.plus(FloatDuration.instantiateSI(5.0f), FloatDuration.instantiateSI(3.0f)));
    }

    /**
     * test minus.
     */
    @Test
    public void testMinus()
    {
        assertEquals(Double.valueOf(2.0), SimTime.minus(Double.valueOf(5.0), Double.valueOf(3.0)), 1E-6);
        assertEquals(Float.valueOf(2.0f), SimTime.minus(Float.valueOf(5.0f), Float.valueOf(3.0f)), 1E-6);
        assertEquals(Long.valueOf(2L), SimTime.minus(Long.valueOf(5L), Long.valueOf(3L)));
        assertEquals(2.0, SimTime.minus(5.0, 3.0), 1E-6);
        assertEquals(2.0f, SimTime.minus(5.0f, 3.0f), 1E-6);
        assertEquals(Long.valueOf(2L), SimTime.minus(5L, 3L));
        assertEquals(Duration.instantiateSI(2.0), SimTime.minus(Duration.instantiateSI(5.0), Duration.instantiateSI(3.0)));
        assertEquals(FloatDuration.instantiateSI(2.0f),
                SimTime.minus(FloatDuration.instantiateSI(5.0f), FloatDuration.instantiateSI(3.0f)));
    }

    /**
     * test copy.
     */
    @Test
    public void testCopy()
    {
        assertEquals(Double.valueOf(5.0), SimTime.copy(Double.valueOf(5.0)), 1E-6);
        assertEquals(Float.valueOf(5.0f), SimTime.copy(Float.valueOf(5.0f)), 1E-6);
        assertEquals(Long.valueOf(5L), SimTime.copy(Long.valueOf(5L)));
        assertEquals(5.0, SimTime.copy(5.0), 1E-6);
        assertEquals(5.0f, SimTime.copy(5.0f), 1E-6);
        assertEquals(Long.valueOf(5L), SimTime.copy(5L));
        assertEquals(Duration.instantiateSI(5.0), SimTime.copy(Duration.instantiateSI(5.0)));
        assertEquals(FloatDuration.instantiateSI(5.0f), SimTime.copy(FloatDuration.instantiateSI(5.0f)));
    }

    /**
     * test zero.
     */
    @Test
    public void testZero()
    {
        assertEquals(Double.valueOf(0.0), SimTime.zero(Double.valueOf(5.0)), 1E-6);
        assertEquals(Float.valueOf(0.0f), SimTime.zero(Float.valueOf(5.0f)), 1E-6);
        assertEquals(Long.valueOf(0L), SimTime.zero(Long.valueOf(5L)));
        assertEquals(0.0, SimTime.zero(5.0), 1E-6);
        assertEquals(0.0f, SimTime.zero(5.0f), 1E-6);
        assertEquals(Long.valueOf(0L), SimTime.zero(5L));
        assertEquals(Duration.instantiateSI(0.0), SimTime.zero(Duration.instantiateSI(5.0)));
        assertEquals(FloatDuration.instantiateSI(0.0f), SimTime.zero(FloatDuration.instantiateSI(5.0f)));
    }

}
