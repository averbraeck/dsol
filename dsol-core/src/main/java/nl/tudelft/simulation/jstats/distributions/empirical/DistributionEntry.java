package nl.tudelft.simulation.jstats.distributions.empirical;

import java.io.Serializable;

/**
 * The Entry contains an actual cumulative probability - value pair.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistributionEntry implements Serializable
{
    /** */
    private static final long serialVersionUID = 20210403L;

    /** the value belonging to the cumulative probability. */
    private final Number value;

    /** the cumulative probability. */
    private final double cumulativeProbability;

    /**
     * Constructs a new Entry, which is a cumulative probability - value pair.
     * @param cumulativeProbability double; the cumulative probability
     * @param value Number; the value belonging to the cumulative probability
     */
    public DistributionEntry(final Number value, final double cumulativeProbability)
    {
        this.value = value;
        this.cumulativeProbability = cumulativeProbability;
    }

    /**
     * Returns the cumulative probability corresponding to this entry.
     * @return double; the cumulative probability corresponding to this entry
     */
    public double getCumulativeProbability()
    {
        return this.cumulativeProbability;
    }

    /**
     * Returns the value corresponding to this entry.
     * @return Number; the value corresponding to this entry
     */
    public Number getValue()
    {
        return this.value;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.cumulativeProbability);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
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
        DistributionEntry other = (DistributionEntry) obj;
        if (Double.doubleToLongBits(this.cumulativeProbability) != Double.doubleToLongBits(other.cumulativeProbability))
            return false;
        if (this.value == null)
        {
            if (other.value != null)
                return false;
        }
        else if (!this.value.equals(other.value))
            return false;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "DistributionEntry [value=" + this.value + ", cumulativeProbability=" + this.cumulativeProbability + "]";
    }

}
