package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Erlang distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/ErlangDistribution.html"> http://mathworld.wolfram.com/ErlangDistribution.html
 * </a><br>
 * The Erlang distribution is the distribution of a sum of k independent exponential variables with the scale parameter as the
 * mean. The scale parameter is equal to 1/rate or 1/&lambda;, giving the entire Erlang distribution a mean of k*scale.
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
public class DistErlang extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * k is the shape parameter of the Erlang distribution. The shape k is the number of times a drawing is done from the
     * exponential distribution, where the Erlang distribution is the sum of these k independent exponential variables.
     */
    private final int k;

    /** scale is the mean of a single exponential distribution (1/rate), of which k are summed. */
    private final double scale;

    /** the rate value of the Erlang distribution (1 / scale). */
    private final double lambda;

    /** distGamma is the underlying gamma distribution. */
    private final DistGamma distGamma;

    /** GAMMATHRESHOLD is the threshold above which we use a gamma function and below repeated drawing. */
    private static final short GAMMATHRESHOLD = 10;

    /**
     * Construct a new Erlang distribution with k and a mean (so not k and a rate) as parameters. It is the distribution of a
     * sum of k independent exponential variables with the scale parameter as the mean. The scale parameter is equal to 1/rate
     * or 1/&lambda;, giving the entire Erlang distribution a mean of k*scale.
     * @param stream StreamInterface; the random number stream
     * @param scale double; the mean of a single sample from the exponential distribution, of which k are summed. Equal to
     *            1/rate or 1/&lambda;.
     * @param k int; the shape parameter of the Erlang distribution. The shape k is the number of times a drawing is done from
     *            the exponential distribution, where the Erlang distribution is the sum of these k independent exponential
     *            variables.
     * @throws IllegalArgumentException when k &lt;= 0 or scale &lt;= 0
     */
    public DistErlang(final StreamInterface stream, final double scale, final int k)
    {
        super(stream);
        Throw.when(k <= 0 || scale <= 0.0, IllegalArgumentException.class, "Error Erlang - k <= 0 or scale <= 0");
        this.k = k;
        this.scale = scale;
        this.lambda = 1.0 / scale;
        this.distGamma = this.k <= DistErlang.GAMMATHRESHOLD ? null : new DistGamma(stream, this.k, this.scale);
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        if (this.k <= DistErlang.GAMMATHRESHOLD)
        {
            // according to Law and Kelton, Simulation Modeling and Analysis
            // repeated drawing and composition is usually faster for k<=10
            double product = 1.0;
            for (int i = 1; i <= this.k; i++)
            {
                product = product * this.stream.nextDouble();
            }
            return -this.scale * Math.log(product);
        }
        // and using the gamma distribution is faster for k>10
        return this.distGamma.draw();
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x < 0)
        {
            return 0;
        }
        return this.lambda * Math.exp(-this.lambda * x) * Math.pow(this.lambda * x, this.k - 1)
                / ProbMath.factorial(this.k - 1);
    }

    /**
     * @return k
     */
    public int getK()
    {
        return this.k;
    }

    /**
     * @return scale parameter
     */
    public double getScale()
    {
        return this.scale;
    }

    /** {@inheritDoc} */
    @Override
    public void setStream(final StreamInterface stream)
    {
        super.setStream(stream);
        if (this.distGamma != null)
        {
            this.distGamma.setStream(stream);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Erlang(" + this.scale + "," + this.k + ")";
    }
}
