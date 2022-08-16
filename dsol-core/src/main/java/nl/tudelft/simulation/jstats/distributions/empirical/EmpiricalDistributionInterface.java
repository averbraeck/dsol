package nl.tudelft.simulation.jstats.distributions.empirical;

import java.io.Serializable;
import java.util.List;

/**
 * The EmpiricalDistributionInterface describes a cumulative distribution function for an empirical distribution. In other
 * words, it describes an ordered list of pairs (cumulative probability, value) from which values can be drawn using the inverse
 * function method with a Uniform(0, 1) random distribution.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface EmpiricalDistributionInterface extends Serializable
{
    /**
     * Returns the number of probability-value mappings in this structure.
     * @return int; the number of probability-value mappings in this structure.
     */
    int size();

    /**
     * Returns the values as a list.
     * @return the list of values
     */
    List<Number> getValues();

    /**
     * Returns the cumulative probabilities as a list.
     * @return the list of cumulative probabilities
     */
    List<Double> getCumulativeProbabilities();

    /**
     * Return the cumulative probability and number below or at the given probability, or null when the cumulative probability
     * is smaller than the lowest cumulative probability.
     * @param cumulativeProbability double; the cumulative probability to look up
     * @return DistributionEntry; an entry object with the probability and corresponding value below or at the provided
     *         cumulative probability. Return null when the cumulative probability is smaller than the lowest cumulative
     *         probability
     */
    DistributionEntry getFloorEntry(double cumulativeProbability);

    /**
     * Return the cumulative probability and number above or at the given probability, or null when the cumulative probability
     * is larger than the highest cumulative probability (1.0).
     * @param cumulativeProbability double; the cumulative probability to look up
     * @return DistributionEntry; an entry object with the probability and corresponding value above or at the provided
     *         cumulative probability. Return null when the cumulative probability is larger than the highest cumulative
     *         probability (1.0)
     */
    DistributionEntry getCeilingEntry(double cumulativeProbability);

    /**
     * Return the cumulative probability and number that precedes in the sequence with respect to the given probability, or null
     * when the cumulative probability is smaller than the lowest cumulative probability. When cumulativeProbability is not a
     * key, the floorEntry is returned instead.
     * @param cumulativeProbability double; the cumulative probability to look up
     * @return DistributionEntry; the first entry object with the probability and corresponding value below (but not at) the
     *         provided cumulative probability. Return null when the cumulative probability is smaller than the lowest
     *         cumulative probability
     */
    DistributionEntry getPrevEntry(double cumulativeProbability);

    /**
     * Return the cumulative probability and number that follows in the sequence with respect to the given probability, or null
     * when the cumulative probability is larger than the highest cumulative probability. When cumulativeProbability is not a
     * key, the ceilingEntry is returned instead.
     * @param cumulativeProbability double; the cumulative probability to look up
     * @return DistributionEntry; the first entry object with the probability and corresponding value above (but not at) the
     *         provided cumulative probability. Return null when the cumulative probability is larger than the highest
     *         cumulative probability
     */
    DistributionEntry getNextEntry(double cumulativeProbability);

    /**
     * Return the cumulative probability and number below or at the given value, or null when the value is smaller than the
     * lowest value in the distribution.
     * @param value Number; the value to look up
     * @return DistributionEntry; an entry object with the probability and corresponding value below or at the provided
     *         cumulative probability. Return null when the cumulative probability is smaller than the lowest cumulative
     *         probability
     */
    DistributionEntry getFloorEntryForValue(Number value);

    /**
     * Return the cumulative probability and number above or at the given value, or null when the value is larger than the
     * highest value in the distribution.
     * @param value Number; the value to look up
     * @return DistributionEntry; an entry object with the probability and corresponding value above or at the provided
     *         cumulative probability. Return null when the cumulative probability is larger than the highest cumulative
     *         probability
     */
    DistributionEntry getCeilingEntryForValue(Number value);

    /**
     * Return the highest value that this empirical distribution can return.
     * @return Number; the highest value that this empirical distribution can return
     */
    Number getHighestValue();

    /**
     * Return the lowest value that this empirical distribution can return.
     * @return Number; the lowest value that this empirical distribution can return
     */
    Number getLowestValue();

}
