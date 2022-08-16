package nl.tudelft.simulation.jstats.distributions.empirical;

import java.util.List;
import java.util.SortedMap;

import org.djutils.exceptions.Throw;

/**
 * CumulativeProbabilities is a helper class to instantiate interpolated and non-interpolated distributions based on a given
 * array or list of values and corresponding cumulative probabilities.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class CumulativeProbabilities
{
    /** Utility class. */
    private CumulativeProbabilities()
    {
        // utility class
    }

    /**
     * Create a discrete empirical distribution based on two arrays of the same length, one with sorted values, and one with
     * corresponding sorted cumulative probabilities. A copy of the original values is made to deal with mutability of the
     * array.
     * @param values Number[] the values
     * @param cumulativeProbabilities double[]; the cumulative probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when cumulativeProbabilities array is null or values array is null, or when one of the
     *             values is null
     * @throws IllegalArgumentException when cumulativeProbabilities array or values array are empty, or have unequal length, or
     *             when cumulativeProbabilities are not between 0 and 1, or when cumulativeProbabilities are not in ascending
     *             order, or when values are not in ascending order, or when the last cumulative probability is not 1.0, or when
     *             the first cumulative probability is zero
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final Number[] values,
            final double[] cumulativeProbabilities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(cumulativeProbabilities, "cumulativeProbabilities array cannot be null");
        return new DiscreteEmpiricalDistribution(values.clone(), cumulativeProbabilities.clone());
    }

    /**
     * Create an interpolated empirical distribution based on two arrays of the same length, one with sorted values, and one
     * with corresponding sorted cumulative probabilities. A copy of the original values is made to deal with mutability of the
     * array.
     * @param values Number[] the values
     * @param cumulativeProbabilities double[]; the cumulative probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when cumulativeProbabilities array is null or values array is null, or when one of the
     *             values is null
     * @throws IllegalArgumentException when cumulativeProbabilities array or values array are empty, or have unequal length, or
     *             when cumulativeProbabilities are not between 0 and 1, or when cumulativeProbabilities are not in ascending
     *             order, or when values are not in ascending order, or when the last cumulative probability is not 1.0, or when
     *             the first cumulative probability is not zero, or there is only one value, so no interpolation can take place
     */
    public static InterpolatedEmpiricalDistribution createInterpolatedDistribution(final Number[] values,
            final double[] cumulativeProbabilities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(cumulativeProbabilities, "cumulativeProbabilities array cannot be null");
        return new InterpolatedEmpiricalDistribution(values.clone(), cumulativeProbabilities.clone());
    }

    /**
     * Create an empirical distribution based on two arrays of the same length, one with sorted values, and one with
     * corresponding sorted cumulative probabilities. A copy of the original values is made to deal with mutability of the
     * array.
     * @param values double[] the values
     * @param cumulativeProbabilities double[]; the cumulative probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when cumulativeProbabilities array is null or values array is null, or when one of the
     *             values is null
     * @throws IllegalArgumentException when cumulativeProbabilities array or values array are empty, or have unequal length, or
     *             when cumulativeProbabilities are not between 0 and 1, or when cumulativeProbabilities are not in ascending
     *             order, or when values are not in ascending order, or when the last cumulative probability is not 1.0, or when
     *             the first cumulative probability is zero
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final double[] values,
            final double[] cumulativeProbabilities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(cumulativeProbabilities, "cumulativeProbabilities array cannot be null");
        Double[] doubleValues = new Double[values.length];
        for (int i = 0; i < values.length; i++)
        {
            doubleValues[i] = values[i];
        }
        return new DiscreteEmpiricalDistribution(doubleValues, cumulativeProbabilities.clone());
    }

    /**
     * Create an empirical distribution based on two arrays of the same length, one with sorted values, and one with
     * corresponding sorted cumulative probabilities. A copy of the original values is made to deal with mutability of the
     * array.
     * @param values double[] the values
     * @param cumulativeProbabilities double[]; the cumulative probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when cumulativeProbabilities array is null or values array is null, or when one of the
     *             values is null
     * @throws IllegalArgumentException when cumulativeProbabilities array or values array are empty, or have unequal length, or
     *             when cumulativeProbabilities are not between 0 and 1, or when cumulativeProbabilities are not in ascending
     *             order, or when values are not in ascending order, or when the last cumulative probability is not 1.0, or when
     *             the first cumulative probability is not zero, or there is only one value, so no interpolation can take place
     */
    public static InterpolatedEmpiricalDistribution createInterpolatedDistribution(final double[] values,
            final double[] cumulativeProbabilities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(cumulativeProbabilities, "cumulativeProbabilities array cannot be null");
        Double[] doubleValues = new Double[values.length];
        for (int i = 0; i < values.length; i++)
        {
            doubleValues[i] = values[i];
        }
        return new InterpolatedEmpiricalDistribution(doubleValues, cumulativeProbabilities.clone());
    }

    /**
     * Create a discrete empirical distribution based on two arrays of the same length, one with sorted values, and one with
     * corresponding sorted cumulative probabilities. A copy of the original values is made to deal with mutability of the
     * array.
     * @param values long[] the values
     * @param cumulativeProbabilities double[]; the cumulative probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when cumulativeProbabilities array is null or values array is null, or when one of the
     *             values is null
     * @throws IllegalArgumentException when cumulativeProbabilities array or values array are empty, or have unequal length, or
     *             when cumulativeProbabilities are not between 0 and 1, or when cumulativeProbabilities are not in ascending
     *             order, or when values are not in ascending order, or when the last cumulative probability is not 1.0, or when
     *             the first cumulative probability is zero
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final long[] values,
            final double[] cumulativeProbabilities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(cumulativeProbabilities, "cumulativeProbabilities array cannot be null");
        Long[] longValues = new Long[values.length];
        for (int i = 0; i < values.length; i++)
        {
            longValues[i] = values[i];
        }
        return new DiscreteEmpiricalDistribution(longValues, cumulativeProbabilities.clone());
    }

    /**
     * Create an interpolated empirical distribution based on two arrays of the same length, one with sorted values, and one
     * with corresponding sorted cumulative probabilities. A copy of the original values is made to deal with mutability of the
     * array.
     * @param values long[] the values
     * @param cumulativeProbabilities double[]; the cumulative probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when cumulativeProbabilities array is null or values array is null, or when one of the
     *             values is null
     * @throws IllegalArgumentException when cumulativeProbabilities array or values array are empty, or have unequal length, or
     *             when cumulativeProbabilities are not between 0 and 1, or when cumulativeProbabilities are not in ascending
     *             order, or when values are not in ascending order, or when the last cumulative probability is not 1.0, or when
     *             the first cumulative probability is not zero, or there is only one value, so no interpolation can take place
     */
    public static InterpolatedEmpiricalDistribution createInterpolatedDistribution(final long[] values,
            final double[] cumulativeProbabilities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(cumulativeProbabilities, "cumulativeProbabilities array cannot be null");
        Long[] longValues = new Long[values.length];
        for (int i = 0; i < values.length; i++)
        {
            longValues[i] = values[i];
        }
        return new InterpolatedEmpiricalDistribution(longValues, cumulativeProbabilities.clone());
    }

    /**
     * Create a discrete empirical distribution based on two Lists of the same length, one with sorted values, and one with
     * corresponding sorted cumulative probabilities.
     * @param values List&lt;? extends Number&gt;; the values
     * @param cumulativeProbabilities List&lt;Double&gt;; the cumulative probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution lists
     * @throws NullPointerException when cumulativeProbabilities list is null, or when values list is null, or when one of the
     *             values is null
     * @throws IllegalArgumentException when cumulativeProbabilities array or values array are empty, or have unequal length, or
     *             when cumulativeProbabilities are not between 0 and 1, or when cumulativeProbabilities are not in ascending
     *             order, or when values are not in ascending order, or when the last cumulative probability is not 1.0, or when
     *             the first cumulative probability is zero
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final List<? extends Number> values,
            final List<Double> cumulativeProbabilities)
    {
        Throw.whenNull(values, "values list cannot be null");
        Throw.whenNull(cumulativeProbabilities, "cumulativeProbabilities list cannot be null");
        return new DiscreteEmpiricalDistribution(values.toArray(new Number[0]),
                cumulativeProbabilities.stream().mapToDouble(d -> d).toArray());
    }

    /**
     * Create an interpolated empirical distribution based on two Lists of the same length, one with sorted values, and one with
     * corresponding sorted cumulative probabilities.
     * @param values List&lt;? extends Number&gt;; the values
     * @param cumulativeProbabilities List&lt;Double&gt;; the cumulative probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution lists
     * @throws NullPointerException when cumulativeProbabilities list is null, or when values list is null, or when one of the
     *             values is null
     * @throws IllegalArgumentException when cumulativeProbabilities array or values array are empty, or have unequal length, or
     *             when cumulativeProbabilities are not between 0 and 1, or when cumulativeProbabilities are not in ascending
     *             order, or when values are not in ascending order, or when the last cumulative probability is not 1.0, or when
     *             the first cumulative probability is not zero, or there is only one value, so no interpolation can take place
     */
    public static InterpolatedEmpiricalDistribution createInterpolatedDistribution(final List<? extends Number> values,
            final List<Double> cumulativeProbabilities)
    {
        Throw.whenNull(values, "values list cannot be null");
        Throw.whenNull(cumulativeProbabilities, "cumulativeProbabilities list cannot be null");
        return new InterpolatedEmpiricalDistribution(values.toArray(new Number[0]),
                cumulativeProbabilities.stream().mapToDouble(d -> d).toArray());
    }

    /**
     * Create a discrete empirical distribution based on a sorted map with sorted values mapping to cumulative probabilities.
     * @param cumulativeProbabilitiesMap SortedMap&lt;Number, Double&gt;; the map with the entries
     * @return the cumulative distribution object belonging to the given distribution map
     * @throws NullPointerException when cumulativeProbabilitiesMap is null, or when one of the values in the map is null
     * @throws IllegalArgumentException when cumulativeProbabilitiesMap is empty, or when cumulativeProbabilities are not
     *             between 0 and 1, or when cumulativeProbabilities are not in ascending order, or when values are not in
     *             ascending order, or when the last cumulative probability is not 1.0, or when the first cumulative probability
     *             is zero
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(
            final SortedMap<? extends Number, Double> cumulativeProbabilitiesMap)
    {
        Throw.whenNull(cumulativeProbabilitiesMap, "cumulativeProbabilitiesMap cannot be null");
        return new DiscreteEmpiricalDistribution(cumulativeProbabilitiesMap.keySet().toArray(new Number[0]),
                cumulativeProbabilitiesMap.values().stream().mapToDouble(d -> d).toArray());
    }

    /**
     * Create an interpolated empirical distribution based on a sorted map with sorted values mapping to cumulative
     * probabilities.
     * @param cumulativeProbabilitiesMap SortedMap&lt;Number, Double&gt;; the map with the entries
     * @return the cumulative distribution object belonging to the given distribution map
     * @throws NullPointerException when cumulativeProbabilitiesMap is null, or when one of the values in the map is null
     * @throws IllegalArgumentException when cumulativeProbabilitiesMap is empty, or when cumulativeProbabilities are not
     *             between 0 and 1, or when cumulativeProbabilities are not in ascending order, or when values are not in
     *             ascending order, or when the last cumulative probability is not 1.0, or when the first cumulative probability
     *             is not zero, or there is only one value, so no interpolation can take place
     */
    public static InterpolatedEmpiricalDistribution createInterpolatedDistribution(
            final SortedMap<? extends Number, Double> cumulativeProbabilitiesMap)
    {
        Throw.whenNull(cumulativeProbabilitiesMap, "cumulativeProbabilitiesMap cannot be null");
        return new InterpolatedEmpiricalDistribution(cumulativeProbabilitiesMap.keySet().toArray(new Number[0]),
                cumulativeProbabilitiesMap.values().stream().mapToDouble(d -> d).toArray());
    }

}
