package nl.tudelft.simulation.jstats.distributions.empirical;

import java.util.List;
import java.util.SortedMap;

import org.djutils.exceptions.Throw;

/**
 * ProbabilityDensities is a helper class to instantiate interpolated and non-interpolated distributions based on a given array
 * or list of values and corresponding probability densities.
 * <p>
 * For a discrete (non-interpolated) distribution, the code is pretty straightforward, as the cumulative distribution is a step
 * function that changes at the given values. Say, that we have an array of 4 values: {1, 2, 3, 5} and probability densities
 * {0.1, 0.4, 0.3, 0.2}. The cumulative distribution function belonging to these densities is (as value, cumulative probability
 * pairs): {(1, 0.1), (2, 0.5), (3, 0.8), (5, 1.0)}, which is a perfect cumulative distribution function for a Discrete
 * distribution, albeit with floating point values.
 * </p>
 * <p>
 * For the interpolated version, it is a bit trickier, since it is not totally intuitive how to interpolate when the value array
 * or list and the density array or list have the same length. Say, that we have an array of 4 values: {1, 2, 3, 5} and
 * probability densities {0.1, 0.4, 0.3, 0.2}.
 * </p>
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class ProbabilityDensities
{
    /** Utility class. */
    private ProbabilityDensities()
    {
        // utility class
    }

    /**
     * Create a discrete empirical distribution, where the probabilities for a value indicate P(value), from two arrays, one
     * with values, and one with corresponding probabilities (summing to 1.0).
     * @param values Number[] the values
     * @param probabilities double[]; the probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when probabilities array is null or values array is null, or when one of the values is null
     * @throws IllegalArgumentException when the probabilities array or values array are empty, or have unequal length, or when
     *             probabilities are not between 0 and 1, or when values are not in ascending order, or when the sum of the
     *             probabilities is not 1.0
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final Number[] values, final double[] probabilities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(probabilities, "probabilities array cannot be null");
        Throw.when(values.length == 0, IllegalArgumentException.class, "values array cannot be empty");
        Throw.when(probabilities.length == 0, IllegalArgumentException.class, "probabilities array cannot be empty");
        Throw.when(probabilities.length != values.length, IllegalArgumentException.class,
                "values array and probabilities array should have the same length");

        double cumulativeProbability = 0;
        double[] cumulativeProbabilities;
        Number[] newValues;
        cumulativeProbabilities = new double[probabilities.length];
        newValues = values.clone();
        for (int i = 0; i < probabilities.length; i++)
        {
            Throw.when(probabilities[i] <= 0.0 || probabilities[i] > 1.0, IllegalArgumentException.class,
                    "probabilities should be between 0 and 1");
            cumulativeProbability += probabilities[i];
            cumulativeProbabilities[i] = cumulativeProbability;
        }
        Throw.when(Math.abs(cumulativeProbability - 1.0) > 10.0 * Math.ulp(1.0), IllegalArgumentException.class,
                "probabilities do not add up to 1.0");
        cumulativeProbabilities[cumulativeProbabilities.length - 1] = 1.0;
        return new DiscreteEmpiricalDistribution(newValues, cumulativeProbabilities);
    }

    /**
     * Create a continuous empirical distribution, where the density for index i indicates that
     * 
     * <pre>
     *    F(value[i+1]) - F(value[i]) = density(i)
     * </pre>
     * 
     * where F(x) is the cumulative distribution function. The empirical distribution is created from two arrays, one with
     * values, and one with corresponding densities The values array has a length that is one more than the length of the
     * densities array. The densities should be such that:
     * 
     * <pre>
     *    &Sigma; {(value[i+1] - value[i]) * density[i]} = 1.0
     * </pre>
     * 
     * @param values Number[] the values
     * @param densities double[]; the densities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when densities array is null or values array is null, or when one of the values is null
     * @throws IllegalArgumentException when densities array or values array are empty, or when values.length !=
     *             (densities.length + 1), or when densities are not between 0 and 1, or when values are not in ascending order,
     *             or when the sum of the probability densities times the value intervals is not 1.0
     */
    public static InterpolatedEmpiricalDistribution createInterpolatedDistribution(final Number[] values,
            final double[] densities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Throw.whenNull(densities, "densities array cannot be null");
        Throw.when(values.length == 0, IllegalArgumentException.class, "values array cannot be empty");
        Throw.when(densities.length == 0, IllegalArgumentException.class, "densities array cannot be empty");
        Throw.when(densities.length + 1 != values.length, IllegalArgumentException.class,
                "length of values array should be 1 + length of densities array");

        double cumulativeProbability = 0;
        double[] cumulativeProbabilities;
        cumulativeProbabilities = new double[values.length];
        cumulativeProbabilities[0] = 0.0;
        for (int i = 0; i < densities.length; i++)
        {
            Throw.when(densities[i] <= 0.0 || densities[i] > 1.0, IllegalArgumentException.class,
                    "densities should be between 0 and 1");
            cumulativeProbability += densities[i] * (values[i + 1].doubleValue() - values[i].doubleValue());
            cumulativeProbabilities[i + 1] = cumulativeProbability;
        }
        Throw.when(Math.abs(cumulativeProbability - 1.0) > 100.0 * Math.ulp(1.0), IllegalArgumentException.class,
                "probabilities do not add up to 1.0");
        cumulativeProbabilities[cumulativeProbabilities.length - 1] = 1.0;
        return new InterpolatedEmpiricalDistribution(values.clone(), cumulativeProbabilities);
    }

    /**
     * Create a discrete empirical distribution, where the probabilities for a value indicate P(value), from two arrays, one
     * with values, and one with corresponding densities (summing to 1.0).
     * @param values double[] the values
     * @param probabilities double[]; the probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when probabilities array is null or values array is null, or when one of the values is null
     * @throws IllegalArgumentException when probabilities array or values array are empty, or have unequal length, or when
     *             probabilities are not between 0 and 1, or when values are not in ascending order, or when the sum of the
     *             probabilities is not 1.0
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final double[] values, final double[] probabilities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Double[] doubleValues = new Double[values.length];
        for (int i = 0; i < values.length; i++)
        {
            doubleValues[i] = values[i];
        }
        return createDiscreteDistribution(doubleValues, probabilities);
    }

    /**
     * Create a continuous empirical distribution, where the density for index i indicates that
     * 
     * <pre>
     *    F(value[i+1]) - F(value[i]) = density(i)
     * </pre>
     * 
     * where F(x) is the cumulative distribution function. The empirical distribution is created from two arrays, one with
     * values, and one with corresponding densities The values array has a length that is one more than the length of the
     * densities array. The densities should be such that:
     * 
     * <pre>
     *    &Sigma; {(value[i+1] - value[i]) * density[i]} = 1.0
     * </pre>
     * 
     * @param values double[] the values
     * @param densities double[]; the densities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when densities array is null or values array is null, or when one of the values is null
     * @throws IllegalArgumentException when densities array or values array are empty, or when values.length !=
     *             (densities.length + 1), or when densities are not between 0 and 1, or when values are not in ascending order,
     *             or when the sum of the probability densities times the value intervals is not 1.0
     */
    public static InterpolatedEmpiricalDistribution createInterpolatedDistribution(final double[] values,
            final double[] densities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Double[] doubleValues = new Double[values.length];
        for (int i = 0; i < values.length; i++)
        {
            doubleValues[i] = values[i];
        }
        return createInterpolatedDistribution(doubleValues, densities);
    }

    /**
     * Create a discrete empirical distribution, where the probabilities for a value indicate P(value), from two arrays, one
     * with values, and one with corresponding probabilities (summing to 1.0).
     * @param values long[] the values
     * @param probabilities double[]; the probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when probabilities array is null or values array is null, or when one of the values is null
     * @throws IllegalArgumentException when probabilities array or values array are empty, or have unequal length, or when
     *             probabilities are not between 0 and 1, or when values are not in ascending order, or when the sum of the
     *             probabilities is not 1.0
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final long[] values, final double[] probabilities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Long[] longValues = new Long[values.length];
        for (int i = 0; i < values.length; i++)
        {
            longValues[i] = values[i];
        }
        return createDiscreteDistribution(longValues, probabilities);
    }

    /**
     * Create a continuous empirical distribution, where the density for index i indicates that
     * 
     * <pre>
     *    F(value[i+1]) - F(value[i]) = density(i)
     * </pre>
     * 
     * where F(x) is the cumulative distribution function. The empirical distribution is created from two arrays, one with
     * values, and one with corresponding densities The values array has a length that is one more than the length of the
     * densities array. The densities should be such that:
     * 
     * <pre>
     *    &Sigma; {(value[i+1] - value[i]) * density[i]} = 1.0
     * </pre>
     * 
     * @param values long[] the values
     * @param densities double[]; the densities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution arrays
     * @throws NullPointerException when densities array is null or values array is null, or when one of the values is null
     * @throws IllegalArgumentException when densities array or values array are empty, or when values.length !=
     *             (densities.length + 1), or when densities are not between 0 and 1, or when values are not in ascending order,
     *             or when the sum of the probability densities times the value intervals is not 1.0
     */
    public static InterpolatedEmpiricalDistribution createInterpolatedDistribution(final long[] values,
            final double[] densities)
    {
        Throw.whenNull(values, "values array cannot be null");
        Long[] longValues = new Long[values.length];
        for (int i = 0; i < values.length; i++)
        {
            longValues[i] = values[i];
        }
        return createInterpolatedDistribution(longValues, densities);
    }

    /**
     * Create a discrete empirical distribution, where the probabilities for a value indicate P(value), based on two Lists of
     * the same length, one with probability probabilities, and one with sorted values.
     * @param values List&lt;? extends Number&gt;; the values
     * @param probabilities List&lt;Double&gt;; the probability probabilities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution lists
     * @throws NullPointerException when probabilities list is null or values list is null, or when one of the values is null
     * @throws IllegalArgumentException when probabilities list or values list are empty, or have unequal length, or when
     *             probabilities are not between 0 and 1, or when values are not in ascending order, or when the sum of the
     *             probabilities is not 1.0
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(final List<? extends Number> values,
            final List<Double> probabilities)
    {
        Throw.whenNull(values, "values list cannot be null");
        Throw.whenNull(probabilities, "probabilities list cannot be null");
        return createDiscreteDistribution(values.toArray(new Number[0]),
                probabilities.stream().mapToDouble(d -> d.doubleValue()).toArray());
    }

    /**
     * Create a continuous empirical distribution, where the density for index i indicates that
     * 
     * <pre>
     *    F(value[i+1]) - F(value[i]) = density(i)
     * </pre>
     * 
     * where F(x) is the cumulative distribution function. The empirical distribution is created from two lists, one with
     * values, and one with corresponding densities The values list has a length that is one more than the length of the
     * densities list. The densities should be such that:
     * 
     * <pre>
     *    &Sigma; {(value[i+1] - value[i]) * density[i]} = 1.0
     * </pre>
     * 
     * @param values List&lt;? extends Number&gt;; the values
     * @param densities List&lt;Double&gt;; the probability densities for the corresponding values
     * @return the cumulative distribution object belonging to the given distribution lists
     * @throws NullPointerException when densities list is null or values list is null, or when one of the values is null
     * @throws IllegalArgumentException when densities list or values list are empty, or when values.length != (densities.length
     *             + 1), or when densities are not between 0 and 1, or when values are not in ascending order, or when the sum
     *             of the probability densities times the value intervals is not 1.0
     */
    public static InterpolatedEmpiricalDistribution createInterpolatedDistribution(final List<? extends Number> values,
            final List<Double> densities)
    {
        Throw.whenNull(values, "values list cannot be null");
        return createInterpolatedDistribution(values.toArray(new Number[0]),
                densities.stream().mapToDouble(d -> d.doubleValue()).toArray());
    }

    /**
     * Create a discrete empirical distribution, where the probabilities for a value indicate P(value), based on a sorted map
     * with sorted values mapping to probability densities.
     * @param densitiesMap SortedMap&lt;? extends Number, Double&gt;; the map with the entries
     * @return the cumulative distribution object belonging to the given distribution map
     * @throws NullPointerException when densities map is null, or when one of the values or densities is null
     * @throws IllegalArgumentException when densities map is empty, or when densities are not between 0 and 1, or when the sum
     *             of the probability densities is not 1.0
     */
    public static DiscreteEmpiricalDistribution createDiscreteDistribution(
            final SortedMap<? extends Number, Double> densitiesMap)
    {
        Throw.whenNull(densitiesMap, "densitiesMap cannot be null");
        return createDiscreteDistribution(densitiesMap.keySet().toArray(new Number[0]),
                densitiesMap.values().stream().mapToDouble(d -> d.doubleValue()).toArray());
    }

}
