package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Pearson5 distribution with a shape parameter &alpha; and a scale parameter &beta;. The distribution is sometimes called
 * the inverse gamma distribution, because if X ~ PT5(&alpha;, &beta;) if and only if Y = 1 / X ~ gamma(&alpha;, 1/&beta;). For
 * more information on this distribution see <a href="https://en.wikipedia.org/wiki/Inverse-gamma_distribution">
 * https://en.wikipedia.org/wiki/Inverse-gamma_distribution</a>
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistPearson5 extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 1L;

    /** dist is the internal gamma distribution for calculation. */
    private final DistGamma dist;

    /** alpha is the shape parameter &alpha; of the distribution. */
    private final double alpha;

    /** beta is the scale parameter &beta; of the distribution. */
    private final double beta;

    /**
     * constructs a new Pearson5 distribution.
     * @param stream StreamInterface; the random number stream
     * @param alpha double; the shape parameter &alpha; of the distribution
     * @param beta double; the scale parameter &beta; of the distribution
     * @throws IllegalArgumentException when alpha &lt;= 0 or beta &lt;= 0
     */
    public DistPearson5(final StreamInterface stream, final double alpha, final double beta)
    {
        super(stream);
        Throw.when(alpha <= 0.0 || beta <= 0.0, IllegalArgumentException.class,
                "Pearson5 distribution cannot be created with alpha <= 0.0 or beta <= 0.0");
        this.alpha = alpha;
        this.beta = beta;
        this.dist = new DistGamma(stream, this.alpha, 1.0d / this.beta);
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        // according to Law and Kelton, Simulation Modeling and Analysis, 1991 pages 492-493
        return 1.0d / this.dist.draw();
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x > 0)
        {
            return Math.pow(this.beta, this.alpha) * Math.pow(x, -this.alpha - 1) * Math.exp(-this.beta / x)
                    / ProbMath.gamma(this.alpha);
        }
        return 0;
    }

    /**
     * Return the shape parameter &alpha; of the distribution.
     * @return double; the shape parameter &alpha; of the distribution
     */
    public double getAlpha()
    {
        return this.alpha;
    }

    /**
     * Return the scale parameter &beta; of the distribution.
     * @return double; the scale parameter &beta; of the distribution
     */
    public double getBeta()
    {
        return this.beta;
    }

    /** {@inheritDoc} */
    @Override
    public void setStream(final StreamInterface stream)
    {
        super.setStream(stream);
        this.dist.setStream(stream);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Pearson5(" + this.alpha + "," + this.beta + ")";
    }
}
