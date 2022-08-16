package nl.tudelft.simulation.jstats.distributions.empirical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Test the EmpiricalDistribution class.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class EmpiricalDistributionTest
{
    /**
     * Test the DistributionEntry object.
     */
    @Test
    public void testDistributionEntry()
    {
        DistributionEntry entry11 = new DistributionEntry(1.0, 0.1);
        DistributionEntry entry12 = new DistributionEntry(1.0, 0.2);
        DistributionEntry entry21 = new DistributionEntry(2.0, 0.1);
        DistributionEntry entry22 = new DistributionEntry(2.0, 0.2);
        DistributionEntry entryN2 = new DistributionEntry(null, 0.2);
        assertEquals(2.0, entry22.getValue().doubleValue(), 1E-6);
        assertEquals(0.2, entry22.getCumulativeProbability(), 1E-6);
        assertEquals(entry11, entry11);
        assertEquals(entry11.hashCode(), entry11.hashCode());
        assertEquals(entry11.toString(), entry11.toString());
        assertNotEquals(entry11, entry12);
        assertNotEquals(entry11.hashCode(), entry12.hashCode());
        assertNotEquals(entry11.toString(), entry12.toString());
        assertNotEquals(entry11, entry21);
        assertNotEquals(entry11, entry22);
        assertNotEquals(null, entry21);
        assertNotEquals(entry11, null);
        assertNotEquals(entry11, new Object());
        assertNotEquals(entryN2, entry22);
        assertNotEquals(entry22, entryN2);
        assertNotEquals(entryN2, entry21);
        assertNotEquals(entry21, entryN2);
        assertNotEquals(entryN2.hashCode(), entry12.hashCode());
        assertNotEquals(entry22.hashCode(), entryN2.hashCode());
        assertEquals(entryN2, new DistributionEntry(null, 0.2));
    }

    /**
     * Test the EmpiricalDistribution object.
     */
    @Test
    public void testHashCodeEquals()
    {
        double[] cpd = {0.1, 0.5, 0.8, 1.0};
        double[] vd = {1.0, 2.0, 3.0, 4.0};
        double[] cpd3 = {0.1, 0.4, 0.8, 1.0};
        double[] vd4 = {1.0, 2.0, 3.5, 4.0};
        DiscreteEmpiricalDistribution def = CumulativeProbabilities.createDiscreteDistribution(vd, cpd);
        DiscreteEmpiricalDistribution def2 = CumulativeProbabilities.createDiscreteDistribution(vd, cpd);
        DiscreteEmpiricalDistribution def3 = CumulativeProbabilities.createDiscreteDistribution(vd, cpd3);
        DiscreteEmpiricalDistribution def4 = CumulativeProbabilities.createDiscreteDistribution(vd4, cpd);
        double[] cpdt = {0.0, 0.5, 0.8, 1.0};
        double[] vdt = {-2.0, 2.0, 3.0, 4.0};
        InterpolatedEmpiricalDistribution det = CumulativeProbabilities.createInterpolatedDistribution(vdt, cpdt);
        assertEquals(def, def2);
        assertEquals(def.hashCode(), def2.hashCode());
        assertEquals(def.toString(), def2.toString());
        assertEquals(def, def);
        assertNotEquals(def, det);
        assertNotEquals(def, null);
        assertNotEquals(def, new Object());
        assertNotEquals(def, def3);
        assertNotEquals(def, def4);
        assertNotEquals(def.hashCode(), def3.hashCode());
        assertNotEquals(def.toString(), def3.toString());
        assertNotEquals(def.hashCode(), def4.hashCode());
        assertNotEquals(def.toString(), def4.toString());
        assertNotEquals(def.hashCode(), det.hashCode());
        assertNotEquals(def.toString(), det.toString());
    }

}
