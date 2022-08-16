package nl.tudelft.simulation.jstats.ode.integrators;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;

/**
 * The RungeKuttaFehlberg.java numerical integrator.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class RungeKuttaFehlberg extends NumericalIntegrator
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the parameters for a_i, in f(x_n + a_i h, .). */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected static double[] a = new double[] {0d, 1d / 4d, 3d / 8d, 12d / 13d, 1d, 1d / 2d};

    /** the parameters for b_ij, in f(., y_n + b_p1 k1 + bp2 k2 + ...). */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected static double[][] b =
            new double[][] {{0d, 0d, 0d, 0d, 0d}, {1d / 4d, 3d / 32d, 1932d / 2197d, 439d / 216d, -8d / 27d},
                    {0d, 9d / 32d, -7200d / 2197d, -8d, 2d}, {0d, 0d, 7296d / 2197d, 3680d / 513d, -3544d / 2565d},
                    {0d, 0d, 0d, -845d / 4104d, 1859d / 4104d}, {0d, 0d, 0d, 0d, -11d / 40d}};

    /** the parameters for c_i, in y_n+1 = y_n + c_1 k_1 + c_2 k_2 + ... */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected static double[] c = new double[] {16d / 135d, 0d, 6656d / 12825d, 28561d / 56430d, -9d / 50d, 2d / 55d};

    /** the parameters for c4_i, in y_n+1 = y_n + c4_1 k_1 + c4_2 k_2 + ... */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected static double[] c4 = new double[] {25d / 216d, 0d, 1408d / 2565d, 2197d / 4104d, -1d / 5d, 0d};

    /** the numer of k-s in the method. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected static int nk = 6;

    /**
     * constructs a new RungeKuttaFehlberg.
     * @param stepSize double; the stepSize
     * @param equation DifferentialEquationInterface; the differentialEquation
     */
    public RungeKuttaFehlberg(final double stepSize, final DifferentialEquationInterface equation)
    {
        super(stepSize, equation);
    }

    /** {@inheritDoc} */
    @Override
    public double[] next(final double x, final double[] y)
    {
        double[][] k = new double[nk][];
        for (int i = 0; i < nk; i++)
        {
            double[] ysum = y.clone();
            for (int j = 0; j < i; j++)
            {
                if (b[i][j] != 0.0)
                {
                    ysum = add(ysum, multiply(b[i][j], k[j]));
                }
            }
            k[i] = multiply(this.stepSize, this.equation.dy(x + a[i] * this.stepSize, ysum));
        }
        double[] sum = y.clone();
        super.error = new double[y.length];
        for (int i = 0; i < nk; i++)
        {
            sum = add(sum, multiply(c[i], k[i]));
            super.error = add(super.error, multiply(c[i] - c4[i], k[i]));
        }
        return sum;
    }
}
