package nl.tudelft.simulation.jstats.distributions;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Truncated Lognormal distribution.
 * <p>
 * (c) copyright 2020-2021 <a href="http://www.simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="http://www.simulation.tudelft.nl"> www.simulation.tudelft.nl </a> <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser General Public License (LGPL) </a>, no warranty.
 * @author <a href="mailto:a.verbraeck@tudelft.nl">Alexander Verbraeck</a> <br>
 */
public class DistLogNormalTrunc extends DistLogNormal
{
    /** */
    private static final long serialVersionUID = 1L;

    /** minimum x-value of the distribution. */
    private final double min;

    /** maximum x-value of the distribution. */
    private final double max;

    /** Non-truncated Lognormal probability density of the min. */
    private final double lognormalProbMin;

    /** Non-truncated Lognormal probability density of the max. */
    private final double lognormalProbMax;

    /**
     * Construct a truncated lognormal distribution with mu=0, sigma=1 with given min and max.
     * @param stream StreamInterface; the random number stream
     * @param min double; minimum x-value of the distribution
     * @param max double; maximum x-value of the distribution
     */
    public DistLogNormalTrunc(final StreamInterface stream, final double min, final double max)
    {
        this(stream, 0.0, 1.0, min, max);
    }

    /**
     * constructs a truncated lognormal distribution with mu and sigma and given min and max.
     * @param stream StreamInterface; the random number stream
     * @param mu double; the mean
     * @param sigma double; the standard deviation
     * @param min double; minimum x-value of the distribution
     * @param max double; maximum x-value of the distribution
     */
    public DistLogNormalTrunc(final StreamInterface stream, final double mu, final double sigma, final double min,
            final double max)
    {
        super(stream, mu, sigma);
        if (max < min)
        {
            throw new IllegalArgumentException("Error Lognormal Truncated - max < min");
        }
        this.min = min;
        this.max = max;
        this.lognormalProbMin = super.getCumulativeProbability(this.min);
        this.lognormalProbMax = super.getCumulativeProbability(this.max);
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        return super.getInverseCumulativeProbability(
                this.lognormalProbMin + (this.lognormalProbMax - this.lognormalProbMin) * this.stream.nextDouble());
    }

    /** {@inheritDoc} */
    @Override
    public double getCumulativeProbability(final double x)
    {
        if (x < this.min)
        {
            return 0.0;
        }
        if (x > this.max)
        {
            return 1.0;
        }
        return (super.getCumulativeProbability(x) - this.lognormalProbMin) / (this.lognormalProbMax - this.lognormalProbMin);
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x < this.min || x > this.max)
        {
            return 0.0;
        }
        return super.getProbabilityDensity(x) / (this.lognormalProbMax - this.lognormalProbMin);
    }

    /** {@inheritDoc} */
    @Override
    public double getInverseCumulativeProbability(final double cumulativeProbability)
    {
        return super.getInverseCumulativeProbability(
                cumulativeProbability * (this.lognormalProbMax - this.lognormalProbMin) + this.lognormalProbMin);
    }

    /**
     * @return min
     */
    public double getMin()
    {
        return this.min;
    }

    /**
     * @return max
     */
    public double getMax()
    {
        return this.max;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "LogNormalTrunc(" + this.mu + "," + this.sigma + "," + this.min + "," + this.max + ")";
    }

}
