package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Normal Truncated distribution. For more information on the truncated normal distribution see <a href=
 * "https://en.wikipedia.org/wiki/Truncated_normal_distribution">https://en.wikipedia.org/wiki/Truncated_normal_distribution</a>
 * <p>
 * This version of the normal distribution uses the numerically approached inverse cumulative distribution.
 * <p>
 * (c) copyright 2002-2021 <a href="http://www.simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="http://www.simulation.tudelft.nl"> www.simulation.tudelft.nl </a> <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser General Public License (LGPL) </a>, no warranty.
 * @author <a href="mailto:a.verbraeck@tudelft.nl"> Alexander Verbraeck </a> <br>
 *         <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public class DistNormalTrunc extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 1L;

    /** mu refers to the mean of the normal distribution. */
    private final double mu;

    /** sigma refers to the standard deviation of the normal distribution. */
    private final double sigma;

    /** minimum x-value of the distribution. */
    private final double min;

    /** maximum x-value of the distribution. */
    private final double max;

    /** cumulative distribution value of the minimum. */
    private final double cumulProbMin;

    /** cumulative distribution value difference between max and min. */
    private final double cumulProbDiff;

    /** factor on probability density to normalize to 1. */
    private final double probDensFactor;

    /**
     * constructs a normal distribution with mu=0 and sigma=1. Errors of various types, e.g., in the impact point of a bomb;
     * quantities that are the sum of a large number of other quantities by the virtue of the central limit theorem.
     * @param stream StreamInterface; the numberstream
     * @param min double; minimum x-value of the distribution
     * @param max double; maximum x-value of the distribution
     */
    public DistNormalTrunc(final StreamInterface stream, final double min, final double max)
    {
        this(stream, 0.0, 1.0, min, max);
    }

    /**
     * constructs a normal distribution with mu and sigma.
     * @param stream StreamInterface; the random number stream
     * @param mu double; the mean
     * @param sigma double; the standard deviation
     * @param min double; minimum x-value of the distribution
     * @param max double; maximum x-value of the distribution
     * @throws IllegalArgumentException when sigma &lt;= 0 or when max &lt;= min, or when the probabilities are so small that
     *             drawing becomes impossible. The cutoff point is at an interval with an overall probability of less than 1E-6
     */
    public DistNormalTrunc(final StreamInterface stream, final double mu, final double sigma, final double min,
            final double max)
    {
        super(stream);
        Throw.when(max < min, IllegalArgumentException.class, "Error Normal Truncated - max < min");
        Throw.when(sigma <= 0, IllegalArgumentException.class, "Error Normal Truncated - sigma <= 0");
        this.mu = mu;
        this.sigma = sigma;
        this.min = min;
        this.max = max;
        this.cumulProbMin = getCumulativeProbabilityNotTruncated(min);
        this.cumulProbDiff = getCumulativeProbabilityNotTruncated(max) - this.cumulProbMin;
        Throw.when(this.cumulProbDiff < 1E-6, IllegalArgumentException.class, "Error " + toString()
                + ": the indicated interval on this normal distribution has a very low probability of " + this.cumulProbDiff);
        this.probDensFactor = 1.0 / this.cumulProbDiff;
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        double d =
                getInverseCumulativeProbabilityNotTruncated(this.cumulProbMin + this.cumulProbDiff * this.stream.nextDouble());
        if (Double.isInfinite(d))
        {
            // if inverse cumulative probability gets close to 1, Infinity is returned.
            // This corresponds to a value of 'max' for the truncated distribution.
            d = d < 0 ? this.min : this.max;
        }
        if (d < this.min)
        {
            // rounding error?
            if (Math.abs(d - this.min) < 1.0E-6 * Math.abs(this.min))
            {
                return this.min;
            }
            throw new IllegalStateException(toString() + ": drawn value outside of interval [min, max]: value " + d
                    + " not in [" + this.min + ", " + this.max + "]");
        }
        if (d > this.max)
        {
            // rounding error?
            if (Math.abs(d - this.max) < 1.0E-6 * Math.abs(this.max))
            {
                return this.min;
            }
            throw new IllegalStateException(toString() + ": drawn value outside of interval [min, max]: value " + d
                    + " not in [" + this.min + ", " + this.max + "]");
        }
        return d;
    }

    /**
     * returns the cumulative probability of the x-value.
     * @param x double; the observation x
     * @return double the cumulative probability
     */
    public double getCumulativeProbability(final double x)
    {
        if (x <= this.min)
        {
            return 0.0;
        }
        if (x >= this.max)
        {
            return 1.0;
        }
        return (getCumulativeProbabilityNotTruncated(x) - this.cumulProbMin) * this.probDensFactor;
    }

    /**
     * returns the cumulative probability of the x-value.
     * @param x double; the observation x
     * @return double the cumulative probability
     */
    private double getCumulativeProbabilityNotTruncated(final double x)
    {
        return 0.5 + 0.5 * ProbMath.erf((x - this.mu) / (Math.sqrt(2.0) * this.sigma));
    }

    /**
     * returns the x-value of the given cumulativeProbability.
     * @param cumulativeProbability double; reflects cum prob
     * @return double the inverse cumulative probability
     */
    private double getInverseCumulativeProbabilityNotTruncated(final double cumulativeProbability)
    {
        return this.mu + this.sigma * Math.sqrt(2.0) * ProbMath.erfInv(2.0 * cumulativeProbability - 1.0);
    }

    /**
     * returns the x-value of the given cumulativePropability.
     * @param cumulativeProbability double; reflects cum prob
     * @return double the inverse cumulative probability
     */
    public double getInverseCumulativeProbability(final double cumulativeProbability)
    {
        if (cumulativeProbability < 0 || cumulativeProbability > 1)
        {
            throw new IllegalArgumentException("1<cumulativeProbability<0 ?");
        }
        /*
         * For extreme cases we return the min and max directly. The method getInverseCumulativeProbabilityNotTruncated() can
         * only return values from "mu - 10*sigma" to "mu + 10*sigma". If min or max is beyond these values, those values would
         * result erroneously. For any cumulative probability that is slightly above 0.0 or slightly below 1.0, values in the
         * range from "mu - 10*sigma" to "mu + 10*sigma" will always result.
         */
        if (cumulativeProbability == 0.0)
        {
            return this.min;
        }
        if (cumulativeProbability == 1.0)
        {
            return this.max;
        }
        return getInverseCumulativeProbabilityNotTruncated(this.cumulProbMin + cumulativeProbability * this.cumulProbDiff);
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x < this.min || x > this.max)
        {
            return 0.0;
        }
        return this.probDensFactor / (Math.sqrt(2 * Math.PI * Math.pow(this.sigma, 2)))
                * Math.exp(-1 * Math.pow(x - this.mu, 2) / (2 * Math.pow(this.sigma, 2)));
    }

    /**
     * @return mu
     */
    public double getMu()
    {
        return this.mu;
    }

    /**
     * @return sigma
     */
    public double getSigma()
    {
        return this.sigma;
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
        return "NormalTrunc(" + this.mu + "," + this.sigma + "," + this.min + "," + this.max + ")";
    }

}
