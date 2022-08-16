package nl.tudelft.simulation.jstats.distributions.empirical;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.djutils.exceptions.Throw;

/**
 * The AbstractEmpiricalDistribution implements the logic for a cumulative distribution function for an empirical distribution.
 * In other words, it describes an ordered list of pairs (cumulative probability, value) from which values can be drawn using
 * the inverse function method with a Uniform(0, 1) random distribution.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractEmpiricalDistribution implements EmpiricalDistributionInterface
{
    /** */
    private static final long serialVersionUID = 20210402L;

    /** the map from cumulative probabilities to values. */
    private final TreeMap<Double, Number> cumulativeProbabilityMap = new TreeMap<>();

    /**
     * Construct the empirical distribution based on two arrays of the same length, one with sorted values, and one with
     * corresponding sorted cumulative probabilities. This constructor assumes that the arrays have been properly cloned to
     * avoid changes to their content after the construction of the distribution. Tests for arrays not being equal to null
     * should have been carried out when calling this constructor.
     * @param values Number[] Number[]; the values belonging to each cumulative probability
     * @param cumulativeProbabilities double[]; the cumulative probabilities
     * @throws NullPointerException when one of the values is null
     * @throws IllegalArgumentException when cumulativeProbabilities array or values array are empty, or have unequal length, or
     *             when cumulativeProbabilities are not between 0 and 1, or when cumulativeProbabilities are not in ascending
     *             order, or when values are not in ascending order, or when the last cumulative probability is not 1.0
     */
    public AbstractEmpiricalDistribution(final Number[] values, final double[] cumulativeProbabilities)
    {
        Throw.when(values.length == 0, IllegalArgumentException.class, "values array cannot be empty");
        Throw.when(cumulativeProbabilities.length == 0, IllegalArgumentException.class,
                "cumulativeProbabilities array cannot be empty");
        Throw.when(cumulativeProbabilities.length != values.length, IllegalArgumentException.class,
                "values array and cumulativeProbabilities array should have the same length");
        double prevCP = -1.0;
        double prevV = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < cumulativeProbabilities.length; i++)
        {
            Throw.whenNull(values[i], "one of the values is null");
            Throw.when(cumulativeProbabilities[i] < 0.0 || cumulativeProbabilities[i] > 1.0, IllegalArgumentException.class,
                    "cumulative probability not between 0 and 1");
            Throw.when(cumulativeProbabilities[i] <= prevCP, IllegalArgumentException.class,
                    "cumulative probabilities not in ascending order");
            Throw.when(values[i].doubleValue() <= prevV, IllegalArgumentException.class, "values not in ascending order");
            prevCP = cumulativeProbabilities[i];
            prevV = values[i].doubleValue();
        }
        Throw.when(cumulativeProbabilities[cumulativeProbabilities.length - 1] != 1.0, IllegalArgumentException.class,
                "last cumulative probability should be 1.0");

        for (int i = 0; i < values.length; i++)
        {
            this.cumulativeProbabilityMap.put(cumulativeProbabilities[i], values[i]);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int size()
    {
        return this.cumulativeProbabilityMap.size();
    }

    /** {@inheritDoc} */
    @Override
    public List<Double> getCumulativeProbabilities()
    {
        return new ArrayList<Double>(this.cumulativeProbabilityMap.keySet());
    }

    /** {@inheritDoc} */
    @Override
    public List<Number> getValues()
    {
        return new ArrayList<Number>(this.cumulativeProbabilityMap.values());
    }

    /** {@inheritDoc} */
    @Override
    public DistributionEntry getFloorEntry(final double cumulativeProbability)
    {
        Map.Entry<Double, Number> floorEntry = this.cumulativeProbabilityMap.floorEntry(cumulativeProbability);
        if (floorEntry == null)
        {
            return null;
        }
        return new DistributionEntry(floorEntry.getValue(), floorEntry.getKey());
    }

    /** {@inheritDoc} */
    @Override
    public DistributionEntry getCeilingEntry(final double cumulativeProbability)
    {
        Map.Entry<Double, Number> ceilingEntry = this.cumulativeProbabilityMap.ceilingEntry(cumulativeProbability);
        if (ceilingEntry == null)
        {
            return null;
        }
        return new DistributionEntry(ceilingEntry.getValue(), ceilingEntry.getKey());
    }

    /** {@inheritDoc} */
    @Override
    public DistributionEntry getPrevEntry(final double cumulativeProbability)
    {
        Map.Entry<Double, Number> floorEntry = this.cumulativeProbabilityMap.floorEntry(cumulativeProbability);
        if (floorEntry != null)
        {
            if (floorEntry.getKey() == cumulativeProbability)
            {
                floorEntry = this.cumulativeProbabilityMap.lowerEntry(floorEntry.getKey());
            }
            if (floorEntry != null)
            {
                return new DistributionEntry(floorEntry.getValue(), floorEntry.getKey());
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public DistributionEntry getNextEntry(final double cumulativeProbability)
    {
        Map.Entry<Double, Number> ceilingEntry = this.cumulativeProbabilityMap.ceilingEntry(cumulativeProbability);
        if (ceilingEntry != null)
        {
            if (ceilingEntry.getKey() == cumulativeProbability)
            {
                ceilingEntry = this.cumulativeProbabilityMap.higherEntry(ceilingEntry.getKey());
            }
            if (ceilingEntry != null)
            {
                return new DistributionEntry(ceilingEntry.getValue(), ceilingEntry.getKey());
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public DistributionEntry getFloorEntryForValue(final Number value)
    {
        Map.Entry<Double, Number> floorEntry = null;
        for (double key : this.cumulativeProbabilityMap.keySet())
        {
            Map.Entry<Double, Number> entry = this.cumulativeProbabilityMap.floorEntry(key);
            if (entry.getValue().equals(value))
            {
                return new DistributionEntry(entry.getValue(), entry.getKey());
            }
            if (entry.getValue().doubleValue() > value.doubleValue())
            {
                break;
            }
            floorEntry = entry;
        }
        return floorEntry == null ? null : new DistributionEntry(floorEntry.getValue(), floorEntry.getKey());
    }

    /** {@inheritDoc} */
    @Override
    public DistributionEntry getCeilingEntryForValue(final Number value)
    {
        for (double key : this.cumulativeProbabilityMap.keySet())
        {
            Map.Entry<Double, Number> entry = this.cumulativeProbabilityMap.floorEntry(key);
            if (entry.getValue().equals(value) || entry.getValue().doubleValue() > value.doubleValue())
            {
                return new DistributionEntry(entry.getValue(), entry.getKey());
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Number getLowestValue()
    {
        return this.cumulativeProbabilityMap.ceilingEntry(-1.0).getValue();
    }

    /** {@inheritDoc} */
    @Override
    public Number getHighestValue()
    {
        return this.cumulativeProbabilityMap.floorEntry(2.0).getValue();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.cumulativeProbabilityMap.hashCode();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEmpiricalDistribution other = (AbstractEmpiricalDistribution) obj;
        if (!this.cumulativeProbabilityMap.equals(other.cumulativeProbabilityMap))
            return false;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "EmpiricalDistribution [cumulativeProbabilityMap=" + this.cumulativeProbabilityMap + "]";
    }

}
