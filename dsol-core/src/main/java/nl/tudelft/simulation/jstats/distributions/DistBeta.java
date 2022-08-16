package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Beta distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/BetaDistribution.html"> https://mathworld.wolfram.com/BetaDistribution.html </a>
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
public class DistBeta extends DistContinuous
{

    /** */
    private static final long serialVersionUID = 1L;

    /** dist1 refers to the first Gamma distribution. */
    private final DistGamma dist1;

    /** dist2 refers to the second Gamma distribution. */
    private final DistGamma dist2;

    /** alpha1 is the first parameter for the Beta distribution. */
    private final double alpha1;

    /** alpha2 is the second parameter for the Beta distribution. */
    private final double alpha2;

    /**
     * constructs a new beta distribution.
     * @param stream StreamInterface; the stream.
     * @param alpha1 double; the first shape parameter &alpha;<sub>1</sub> for the distribution
     * @param alpha2 double; the second shape parameter &alpha;<sub>2</sub>for the distribution
     * @throws IllegalArgumentException when alpha1 &lt;= 0.0 or alpha2 &lt;= 0.0
     */
    public DistBeta(final StreamInterface stream, final double alpha1, final double alpha2)
    {
        super(stream);
        Throw.when(alpha1 <= 0.0 || alpha2 <= 0.0, IllegalArgumentException.class, "Error alpha1 <= 0.0 or alpha2 <= 0.0");
        this.alpha1 = alpha1;
        this.alpha2 = alpha2;
        this.dist1 = new DistGamma(stream, this.alpha1, 1.0);
        this.dist2 = new DistGamma(stream, this.alpha2, 1.0);
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        // according to Law and Kelton, Simulation Modeling and Analysis, 1991, pages 492-493
        double y1 = this.dist1.draw();
        double y2 = this.dist2.draw();
        return y1 / (y1 + y2);
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x > 0 && x < 1)
        {
            return (Math.pow(x, this.alpha1 - 1) * Math.pow(1 - x, this.alpha2 - 1)) / ProbMath.beta(this.alpha1, this.alpha2);
        }
        return 0;
    }

    /**
     * Return the first shape parameter &alpha;<sub>1</sub> for the distribution.
     * @return double; the first shape parameter &alpha;<sub>1</sub> for the distribution
     */
    public double getAlpha1()
    {
        return this.alpha1;
    }

    /**
     * Return the second shape parameter &alpha;<sub>2</sub>for the distribution.
     * @return double; the second shape parameter &alpha;<sub>2</sub>for the distribution
     */
    public double getAlpha2()
    {
        return this.alpha2;
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
        return "Beta(" + this.alpha1 + "," + this.alpha2 + ")";
    }
}
