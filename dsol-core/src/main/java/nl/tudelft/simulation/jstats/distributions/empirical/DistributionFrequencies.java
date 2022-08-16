package nl.tudelft.simulation.jstats.distributions.empirical;

import java.util.List;
import java.util.SortedMap;

import org.djutils.exceptions.Throw;

/**
 * DistributionFrequencies is a helper class to instantiate interpolated and non-interpolated distributions based on a given
 * array or list of values and corresponding frequencies (integer valued) or weights (real valued).
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class DistributionFrequencies
{
    /** Utility class. */
    private DistributionFrequencies()
    {
        // utility class
    }

    /**
     * Create a discrete empirical distribution from two arrays, one with frequencies or weights, and one with corresponding
     * values.
     * @param values Number[] the values
     * @param frequencies long[]; the frequencies for the corresponding values
     * @return the cumulative distribution object belonging to the given arrays
     * @throws NullPointerException when values array is null or frequencies array is null, or when one of the values is null
     * @throws IllegalArgumentException when frequencies array or values array are empty, or have unequal length, or when
     *             frequencies are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final Number[] values, final long[] frequencies)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(frequencies, "frequencies array cannot be null");
        Throw.when(values.length == 0, IllegalArgumentException.class, "values array cannot be empty");
        Throw.when(frequencies.length == 0, IllegalArgumentException.class, "frequencies array cannot be empty");
        Throw.when(frequencies.length != values.length, IllegalArgumentException.class,
                "values array and frequencies array should have the same length");

        double sum = 0.0;
        for (int i = 0; i < frequencies.length; i++)
        {
            Throw.when(frequencies[i] <= 0, IllegalArgumentException.class, "frequency cannot be zero or negative");
            sum += 1.0 * frequencies[i];
        }

        double[] cumulativeProbabilities;
        double partialSum = 0;
        cumulativeProbabilities = new double[frequencies.length];
        for (int i = 0; i < frequencies.length; i++)
        {
            partialSum += 1.0 * frequencies[i];
            cumulativeProbabilities[i] = partialSum / sum;
        }
        cumulativeProbabilities[cumulativeProbabilities.length - 1] = 1.0;
        return new DiscreteEmpiricalDistribution(values.clone(), cumulativeProbabilities);
    }

    /**
     * Create a discrete empirical distribution from two arrays, one with frequencies or weights, and one with corresponding
     * values.
     * @param values double[] the values
     * @param frequencies long[]; the frequencies for the corresponding values
     * @return the cumulative distribution object belonging to the given arrays
     * @throws NullPointerException when values array is null or frequencies array is null, or when one of the values is null
     * @throws IllegalArgumentException when frequencies array or values array are empty, or have unequal length, or when
     *             frequencies are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final double[] values, final long[] frequencies)
    {
        Throw.whenNull(values, "values array cannot be null");
        Double[] doubleValues = new Double[values.length];
        for (int i = 0; i < values.length; i++)
        {
            doubleValues[i] = values[i];
        }
        return createDiscreteDistribution(doubleValues, frequencies);
    }

    /**
     * Create a discrete empirical distribution from two arrays, one with frequencies or weights, and one with corresponding
     * values.
     * @param values long[] the values
     * @param frequencies long[]; the frequencies for the corresponding values
     * @return the cumulative distribution object belonging to the given arrays
     * @throws NullPointerException when values array is null or frequencies array is null, or when one of the values is null
     * @throws IllegalArgumentException when frequencies array or values array are empty, or have unequal length, or when
     *             frequencies are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final long[] values, final long[] frequencies)
    {
        Throw.whenNull(values, "values array cannot be null");
        Long[] longValues = new Long[values.length];
        for (int i = 0; i < values.length; i++)
        {
            longValues[i] = values[i];
        }
        return createDiscreteDistribution(longValues, frequencies);
    }

    /**
     * Create a discrete empirical distribution from two arrays, one with frequencies or weights, and one with corresponding
     * values.
     * @param values Number[] the values
     * @param frequencies int[]; the frequencies for the corresponding values
     * @return the cumulative distribution object belonging to the given arrays
     * @throws NullPointerException when values array is null or frequencies array is null, or when one of the values is null
     * @throws IllegalArgumentException when frequencies array or values array are empty, or have unequal length, or when
     *             frequencies are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final Number[] values, final int[] frequencies)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(frequencies, "frequencies array cannot be null");
        Throw.when(values.length == 0, IllegalArgumentException.class, "values array cannot be empty");
        Throw.when(frequencies.length == 0, IllegalArgumentException.class, "frequencies array cannot be empty");
        Throw.when(frequencies.length != values.length, IllegalArgumentException.class,
                "values array and frequencies array should have the same length");

        double sum = 0.0;
        for (int i = 0; i < frequencies.length; i++)
        {
            Throw.when(frequencies[i] <= 0, IllegalArgumentException.class, "frequency cannot be zero or negative");
            sum += 1.0 * frequencies[i];
        }

        double[] cumulativeProbabilities;
        double partialSum = 0;
        cumulativeProbabilities = new double[frequencies.length];
        for (int i = 0; i < frequencies.length; i++)
        {
            partialSum += 1.0 * frequencies[i];
            cumulativeProbabilities[i] = partialSum / sum;
        }
        cumulativeProbabilities[cumulativeProbabilities.length - 1] = 1.0;
        return new DiscreteEmpiricalDistribution(values.clone(), cumulativeProbabilities);
    }

    /**
     * Create a discrete empirical distribution from two arrays, one with frequencies or weights, and one with corresponding
     * values.
     * @param values double[] the values
     * @param frequencies int[]; the frequencies for the corresponding values
     * @return the cumulative distribution object belonging to the given arrays
     * @throws NullPointerException when values array is null or frequencies array is null, or when one of the values is null
     * @throws IllegalArgumentException when frequencies array or values array are empty, or have unequal length, or when
     *             frequencies are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final double[] values, final int[] frequencies)
    {
        Throw.whenNull(values, "values array cannot be null");
        Double[] doubleValues = new Double[values.length];
        for (int i = 0; i < values.length; i++)
        {
            doubleValues[i] = values[i];
        }
        return createDiscreteDistribution(doubleValues, frequencies);
    }

    /**
     * Create a discrete empirical distribution from two arrays, one with frequencies or weights, and one with corresponding
     * values.
     * @param values long[] the values
     * @param frequencies int[]; the frequencies for the corresponding values
     * @return the cumulative distribution object belonging to the given arrays
     * @throws NullPointerException when values array is null or frequencies array is null, or when one of the values is null
     * @throws IllegalArgumentException when frequencies array or values array are empty, or have unequal length, or when
     *             frequencies are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final long[] values, final int[] frequencies)
    {
        Throw.whenNull(values, "values array cannot be null");
        Long[] longValues = new Long[values.length];
        for (int i = 0; i < values.length; i++)
        {
            longValues[i] = values[i];
        }
        return createDiscreteDistribution(longValues, frequencies);
    }

    /**
     * Create a discrete empirical distribution based on two Lists of the same length, one with sorted values, and one with
     * corresponding frequencies or weights.
     * @param values List&lt;? extends Number&gt;; the values
     * @param frequencies List&lt;? extends Number&gt;; the frequencies or weights for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution lists
     * @throws NullPointerException when frequencies list is null or values list is null, or when one of the values is null
     * @throws IllegalArgumentException when frequencies list or values list are empty, or have unequal length, or when
     *             frequencies are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final List<? extends Number> values,
            final List<? extends Number> frequencies)
    {
        Throw.whenNull(values, "values list cannot be null");
        Throw.whenNull(frequencies, "frequencies list cannot be null");
        Throw.when(values.isEmpty(), IllegalArgumentException.class, "values list cannot be empty");
        Throw.when(frequencies.isEmpty(), IllegalArgumentException.class, "frequencies list cannot be empty");
        Throw.when(frequencies.size() != values.size(), IllegalArgumentException.class,
                "values list and frequencies list should have the same size");

        double sum = 0.0;
        for (int i = 0; i < frequencies.size(); i++)
        {
            Throw.when(frequencies.get(i).doubleValue() <= 0, IllegalArgumentException.class,
                    "frequency cannot be zero or negative");
            sum += 1.0 * frequencies.get(i).doubleValue();
        }

        double[] cumulativeProbabilities;
        double partialSum = 0;
        cumulativeProbabilities = new double[frequencies.size()];
        for (int i = 0; i < frequencies.size(); i++)
        {
            partialSum += frequencies.get(i).doubleValue();
            cumulativeProbabilities[i] = partialSum / sum;
        }
        cumulativeProbabilities[cumulativeProbabilities.length - 1] = 1.0;
        return new DiscreteEmpiricalDistribution(values.toArray(new Number[0]), cumulativeProbabilities);
    }

    /**
     * Create a discrete empirical distribution based on a sorted map with sorted values mapping to frequencies.
     * @param frequenciesMap SortedMap&lt;? extends Number, ? extends Number&gt;; the map with the entries
     * @return the cumulative distribution object belonging to the given distribution map
     * @throws NullPointerException when frequencies map is null, or when one of the values or frequencies is null
     * @throws IllegalArgumentException when frequencies map is empty, or when frequencies are not between 0 and 1, or when the
     *             sum of the probability frequencies is not 1.0
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(
            final SortedMap<? extends Number, ? extends Number> frequenciesMap)
    {
        Throw.whenNull(frequenciesMap, "frequenciesMap cannot be null");
        return createDiscreteDistribution(frequenciesMap.keySet().toArray(new Number[0]),
                frequenciesMap.values().toArray(new Number[0]));
    }

    /**
     * Create a discrete empirical distribution from two arrays, one with weights, and one with corresponding values.
     * @param values Number[] the values
     * @param weights Number[]; the weights for the corresponding values
     * @return the cumulative distribution object belonging to the given arrays
     * @throws NullPointerException when values array is null or weights array is null, or when one of the values is null
     * @throws IllegalArgumentException when weights array or values array are empty, or have unequal length, or when weights
     *             are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final Number[] values, final Number[] weights)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(weights, "weights array cannot be null");
        double[] doubleWeights = new double[weights.length];
        for (int i = 0; i < weights.length; i++)
        {
            doubleWeights[i] = weights[i].doubleValue();
        }
        return createDiscreteDistribution(values, doubleWeights);
    }

    /**
     * Create a discrete empirical distribution from two arrays, one with weights, and one with corresponding values.
     * @param values Number[] the values
     * @param weights double[]; the weights for the corresponding values
     * @return the cumulative distribution object belonging to the given arrays
     * @throws NullPointerException when values array is null or weights array is null, or when one of the values is null
     * @throws IllegalArgumentException when weights array or values array are empty, or have unequal length, or when weights
     *             are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final Number[] values, final double[] weights)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(weights, "weights array cannot be null");
        Throw.when(values.length == 0, IllegalArgumentException.class, "values array cannot be empty");
        Throw.when(weights.length == 0, IllegalArgumentException.class, "weights array cannot be empty");
        Throw.when(weights.length != values.length, IllegalArgumentException.class,
                "values array and weights array should have the same length");

        double sum = 0.0;
        for (int i = 0; i < weights.length; i++)
        {
            Throw.when(weights[i] <= 0, IllegalArgumentException.class, "weight cannot be zero or negative");
            sum += 1.0 * weights[i];
        }

        double[] cumulativeProbabilities;
        double partialSum = 0;
        cumulativeProbabilities = new double[weights.length];
        for (int i = 0; i < weights.length; i++)
        {
            partialSum += 1.0 * weights[i];
            cumulativeProbabilities[i] = partialSum / sum;
        }
        cumulativeProbabilities[cumulativeProbabilities.length - 1] = 1.0;
        return new DiscreteEmpiricalDistribution(values.clone(), cumulativeProbabilities);
    }

    /**
     * Create a discrete empirical distribution from two arrays, one with weights, and one with corresponding values.
     * @param values double[] the values
     * @param weights int[]; the weights for the corresponding values
     * @return the cumulative distribution object belonging to the given arrays
     * @throws NullPointerException when values array is null or weights array is null, or when one of the values is null
     * @throws IllegalArgumentException when weights array or values array are empty, or have unequal length, or when weights
     *             are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final double[] values, final double[] weights)
    {
        Throw.whenNull(values, "values array cannot be null");
        Double[] doubleValues = new Double[values.length];
        for (int i = 0; i < values.length; i++)
        {
            doubleValues[i] = values[i];
        }
        return createDiscreteDistribution(doubleValues, weights);
    }

    /**
     * Create a discrete empirical distribution from two arrays, one with weights, and one with corresponding values.
     * @param values long[] the values
     * @param weights int[]; the weights for the corresponding values
     * @return the cumulative distribution object belonging to the given arrays
     * @throws NullPointerException when values array is null or weights array is null, or when one of the values is null
     * @throws IllegalArgumentException when weights array or values array are empty, or have unequal length, or when weights
     *             are zero or negative, or when values are not in ascending order
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final long[] values, final double[] weights)
    {
        Throw.whenNull(values, "values array cannot be null");
        Long[] longValues = new Long[values.length];
        for (int i = 0; i < values.length; i++)
        {
            longValues[i] = values[i];
        }
        return createDiscreteDistribution(longValues, weights);
    }

}
