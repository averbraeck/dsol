package nl.tudelft.simulation.jstats.distributions.empirical;

import org.djutils.exceptions.Throw;

/**
 * The InterpolatedEmpiricalDistribution implements the logic for a cumulative distribution function for an empirical
 * distribution, where the values will be interpolated between the values. In other words, it describes an ordered list of pairs
 * (cumulative probability, value) from which values can be drawn using the inverse function method with a Uniform(0, 1) random
 * distribution.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InterpolatedEmpiricalDistribution extends AbstractEmpiricalDistribution
{
    /** */
    private static final long serialVersionUID = 20210405L;

    /**
     * Construct the empirical distribution based on two arrays of the same length, one with sorted values, and one with
     * corresponding sorted cumulative probabilities. This constructor assumes that the arrays have been properly cloned to
     * avoid changes to their content after the construction of the distribution. Tests for arrays not being equal to null
     * should have been carried out when calling this constructor. Since the values will be interpolated, the first cumulative
     * probability has to be 0.0, and the last cumulative probability (as always) 1.0.
     * @param values Number[] Number[]; the values belonging to each cumulative probability
     * @param cumulativeProbabilities double[]; the cumulative probabilities
     * @throws NullPointerException when one of the values is null
     * @throws IllegalArgumentException when cumulativeProbabilities array or values array are empty, or have unequal length, or
     *             when cumulativeProbabilities are not between 0 and 1, or when cumulativeProbabilities are not in ascending
     *             order, or when values are not in ascending order, or when the last cumulative probability is not 1.0, or when
     *             the first cumulative probability is not zero, or there is only one value when interpolation is used
     */
    public InterpolatedEmpiricalDistribution(final Number[] values, final double[] cumulativeProbabilities)
    {
        super(values, cumulativeProbabilities);
        Throw.when(cumulativeProbabilities[0] != 0.0, IllegalArgumentException.class,
                "interpolation, but first cumulative probability is not zero");
        Throw.when(cumulativeProbabilities.length < 2, IllegalArgumentException.class,
                "interpolation needs at least two cumulative probability values");
    }

}
