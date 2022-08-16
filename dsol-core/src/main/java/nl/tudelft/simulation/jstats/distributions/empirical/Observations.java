package nl.tudelft.simulation.jstats.distributions.empirical;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djutils.exceptions.Throw;

/**
 * Observations is a helper class to instantiate non-interpolated distributions based on a list or array of observations, from
 * which a distribution is generated. The empirical distribution samples from the set of original values.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class Observations
{
    /** Utility class. */
    private Observations()
    {
        // utility class
    }

    /**
     * Create an empirical distribution from an array with observations.
     * @param observations Number[] the observations
     * @return the cumulative distribution object belonging to the given observations
     * @throws NullPointerException when observations array is null, or when one of the observations is null
     * @throws IllegalArgumentException when observations array is empty
     */
    public static DiscreteEmpiricalDistribution createFromObservations(final Number[] observations)
    {
        Throw.whenNull(observations, "observations array cannot be null");
        SortedMap<Number, Integer> map = new TreeMap<>();
        for (Number n : observations)
        {
            Throw.whenNull(n, "observation cannot be null");
            if (map.containsKey(n))
            {
                map.put(n, map.get(n) + 1);
            }
            else
            {
                map.put(n, 1);
            }
        }
        return DistributionFrequencies.createDiscreteDistribution(map.keySet().toArray(new Number[0]),
                map.values().toArray(new Integer[0]));
    }

    /**
     * Create an empirical distribution from an array with observations.
     * @param observations double[] the observations
     * @return the cumulative distribution object belonging to the given observations
     * @throws NullPointerException when observations array is null, or when one of the observations is null
     * @throws IllegalArgumentException when observations array is empty
     */
    public static DiscreteEmpiricalDistribution createFromObservations(final double[] observations)
    {
        Throw.whenNull(observations, "observations array cannot be null");
        SortedMap<Double, Integer> map = new TreeMap<>();
        for (Double n : observations)
        {
            Throw.whenNull(n, "observation cannot be null");
            if (map.containsKey(n))
            {
                map.put(n, map.get(n) + 1);
            }
            else
            {
                map.put(n, 1);
            }
        }
        return DistributionFrequencies.createDiscreteDistribution(map.keySet().toArray(new Number[0]),
                map.values().toArray(new Integer[0]));
    }

    /**
     * Create an empirical distribution from an array with observations.
     * @param observations long[] the observations
     * @return the cumulative distribution object belonging to the given observations
     * @throws NullPointerException when observations array is null, or when one of the observations is null
     * @throws IllegalArgumentException when observations array is empty
     */
    public static DiscreteEmpiricalDistribution createFromObservations(final long[] observations)
    {
        Throw.whenNull(observations, "observations array cannot be null");
        SortedMap<Long, Integer> map = new TreeMap<>();
        for (Long n : observations)
        {
            Throw.whenNull(n, "observation cannot be null");
            if (map.containsKey(n))
            {
                map.put(n, map.get(n) + 1);
            }
            else
            {
                map.put(n, 1);
            }
        }
        return DistributionFrequencies.createDiscreteDistribution(map.keySet().toArray(new Number[0]),
                map.values().toArray(new Integer[0]));
    }

    /**
     * Create an empirical distribution from a list with observations.
     * @param observations List&lt;Number&gt;; the observations
     * @return the cumulative distribution object belonging to the given observations
     * @throws NullPointerException when observations list is null, or when one of the observations is null
     * @throws IllegalArgumentException when observations list is empty
     */
    public static DiscreteEmpiricalDistribution createFromObservations(final List<? extends Number> observations)
    {
        Throw.whenNull(observations, "observations list cannot be null");
        SortedMap<Number, Integer> map = new TreeMap<>();
        for (Number n : observations)
        {
            if (map.containsKey(n))
            {
                map.put(n, map.get(n) + 1);
            }
            else
            {
                map.put(n, 1);
            }
        }
        return DistributionFrequencies.createDiscreteDistribution(map.keySet().toArray(new Number[0]),
                map.values().toArray(new Integer[0]));
    }

}
