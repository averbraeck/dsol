package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Gamma distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/GammaDistribution.html"> https://mathworld.wolfram.com/GammaDistribution.html </a><br>
 * The parameters are not rate-related, but average-related, so the mean is shape*scale (or &alpha;&theta;), and the variance is
 * &alpha;&theta;<sup>2</sup>.
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
public class DistGamma extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the shape parameter of the distribution, also often called &alpha; or k. */
    private final double shape;

    /** the scale parameter of the distribution, also often called &theta;. */
    private final double scale;

    /**
     * constructs a new gamma distribution. The gamma distribution represents the time to complete some task, e.g. customer
     * service or machine repair. The parameters are not rate-related, but average-related, so the mean is shape*scale (or
     * &alpha;&theta; or k&theta;), and the variance is &alpha;&theta;<sup>2</sup>.
     * @param stream StreamInterface; the random number stream
     * @param shape double; is the shape parameter &gt; 0, also known as &alpha; or k
     * @param scale double; is the scale parameter&gt; 0, also known as &theta;
     * @throws IllegalArgumentException when shape &lt;= 0.0 or scale &lt;= 0.0
     */
    public DistGamma(final StreamInterface stream, final double shape, final double scale)
    {
        super(stream);
        Throw.when(shape <= 0.0 || scale <= 0.0, IllegalArgumentException.class, "Error Gamma - shape <= 0.0 or scale <= 0.0");
        this.shape = shape;
        this.scale = scale;
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        // according to Law and Kelton, Simulation Modeling and Analysis, 1991
        // pages 488-489
        if (this.shape < 1.0)
        {
            double b = (Math.E + this.shape) / Math.E;
            int counter = 0;
            while (counter < 1000)
            {
                // step 1.
                double p = b * this.stream.nextDouble();
                if (p <= 1.0d)
                {
                    // step 2.
                    double y = Math.pow(p, 1.0d / this.shape);
                    double u2 = this.stream.nextDouble();
                    if (u2 <= Math.exp(-y))
                    {
                        return this.scale * y;
                    }
                }
                else
                {
                    // step 3.
                    double y = -Math.log((b - p) / this.shape);
                    double u2 = this.stream.nextDouble();
                    if (u2 <= Math.pow(y, this.shape - 1.0d))
                    {
                        return this.scale * y;
                    }
                }
                counter++;
            }
            CategoryLogger.always().info("Gamma distribution -- 1000 tries for alpha<1.0");
            return 1.0d;
        }
        else if (this.shape > 1.0)
        {
            // according to Law and Kelton, Simulation Modeling and
            // Analysis, 1991, pages 488-489
            double a = 1.0d / Math.sqrt(2.0d * this.shape - 1.0d);
            double b = this.shape - Math.log(4.0d);
            double q = this.shape + (1.0d / a);
            double theta = 4.5d;
            double d = 1.0d + Math.log(theta);
            int counter = 0;
            while (counter < 1000)
            {
                // step 1.
                double u1 = this.stream.nextDouble();
                double u2 = this.stream.nextDouble();
                // step 2.
                double v = a * Math.log(u1 / (1.0d - u1));
                double y = this.shape * Math.exp(v);
                double z = u1 * u1 * u2;
                double w = b + q * v - y;
                // step 3.
                if ((w + d - theta * z) >= 0.0d)
                {
                    return this.scale * y;
                }
                // step 4.
                if (w > Math.log(z))
                {
                    return this.scale * y;
                }
                counter++;
            }
            CategoryLogger.always().info("Gamma distribution -- 1000 tries for alpha>1.0");
            return 1.0d;
        }
        else
        // shape == 1.0
        {
            // Gamma(1.0, scale) ~ exponential with mean = scale
            return -this.scale * Math.log(this.stream.nextDouble());
        }
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x <= 0)
        {
            return 0.0;
        }
        return (Math.pow(this.scale, -this.shape) * Math.pow(x, this.shape - 1) * Math.exp(-1 * x / this.scale))
                / ProbMath.gamma(this.shape);
    }

    /**
     * @return alpha
     */
    public double getShape()
    {
        return this.shape;
    }

    /**
     * @return beta
     */
    public double getScale()
    {
        return this.scale;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Gamma(" + this.shape + "," + this.scale + ")";
    }
}
