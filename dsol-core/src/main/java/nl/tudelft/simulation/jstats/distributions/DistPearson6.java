package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Pearson6 distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/Pearson6Distribution.html"> https://mathworld.wolfram.com/Pearson6Distribution.html
 * </a>
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
public class DistPearson6 extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 1L;

    /** dist1 is the first gamma distribution. */
    private final DistGamma dist1;

    /** dist2 is the second gamma distribution. */
    private final DistGamma dist2;

    /** alpha1 is the first shape parameter. */
    private final double alpha1;

    /** alpha2 is the second shape parameter. */
    private final double alpha2;

    /** beta is the scale parameter. */
    private final double beta;

    /**
     * constructs a new Pearson6 distribution.
     * @param stream StreamInterface; the random number stream
     * @param alpha1 double; the first shape parameter
     * @param alpha2 double; the second shape parameter
     * @param beta double; the scale parameter
     * @throws IllegalArgumentException when alpha1 &lt;= 0 or alpha2 &lt;= 0 or beta &lt;= 0
     */
    public DistPearson6(final StreamInterface stream, final double alpha1, final double alpha2, final double beta)
    {
        super(stream);
        Throw.when(alpha1 <= 0.0 || alpha2 <= 0.0 || beta <= 0.0, IllegalArgumentException.class,
                "Pearson6 distribution cannot be created with alpha1 <= 0.0 or alpha2 <= 0 or beta <= 0.0");
        this.alpha1 = alpha1;
        this.alpha2 = alpha2;
        this.beta = beta;
        this.dist1 = new DistGamma(super.stream, this.alpha1, this.beta);
        this.dist2 = new DistGamma(super.stream, this.alpha2, this.beta);
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        // according to Law and Kelton, Simulation Modeling and Analysis, 1991, page 494.
        // but since dist1 and dist2 are both scaled by beta, the result is beta/beta = 1, without the scale parameter
        // So, in contrast with Law & Kelton and Banks (2000), a multiplication with beta is added
        return this.beta * this.dist1.draw() / this.dist2.draw();
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x > 0)
        {
            return Math.pow(x / this.beta, this.alpha1 - 1) / (this.beta * ProbMath.beta(this.alpha1, this.alpha2)
                    * Math.pow(1 + x / this.beta, this.alpha1 + this.alpha2));
        }
        return 0;
    }

    /**
     * Return the first shape parameter &alpha;1.
     * @return double; the first shape parameter &alpha;1
     */
    public double getAlpha1()
    {
        return this.alpha1;
    }

    /**
     * Return the second shape parameter &alpha;2.
     * @return double; the second shape parameter &alpha;2
     */
    public double getAlpha2()
    {
        return this.alpha2;
    }

    /**
     * Return the scale parameter &beta;.
     * @return double; the scale parameter &beta;
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
        this.dist1.setStream(stream);
        this.dist2.setStream(stream);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Pearson6(" + this.alpha1 + "," + this.alpha2 + "," + this.beta + ")";
    }

}
