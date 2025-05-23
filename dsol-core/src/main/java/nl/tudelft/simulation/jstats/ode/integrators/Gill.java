package nl.tudelft.simulation.jstats.ode.integrators;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;

/**
 * The Gill numerical estimator as described in <a href="https://mathworld.wolfram.com/GillsMethod.html">
 * https://mathworld.wolfram.com/GillsMethod.html </a>
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Gill extends NumericalIntegrator
{
    /** */
    private static final long serialVersionUID = 1L;

    /** constant: sqrt(2). */
    private static final double SQRT2 = Math.sqrt(2.0d);

    /** constant: 1/2 sqrt(2). */
    private static final double SQRT2D2 = 0.5d * SQRT2;

    /** constant: 2.0 - sqrt(2). */
    private static final double M2SQRT2 = 2.0d - SQRT2;

    /** constant: 2.0 + sqrt(2). */
    private static final double P2SQRT2 = 2.0d + SQRT2;

    /** constant: 1.0 - 0.5 * sqrt(2). */
    private static final double IMSQRT2D2 = 1.0d - SQRT2D2;

    /** constant: 1.0 + 0.5 * sqrt(2). */
    private static final double IPSQRT2D2 = 1.0d + SQRT2D2;

    /** constant: 0.5 * (-1 + sqrt(2)). */
    private static final double HM1SQRT2 = 0.5d * (-1.0d + SQRT2);

    /**
     * constructs a new Gill integrator.
     * @param stepSize the stepSize
     * @param equation the differentialEquation
     */
    public Gill(final double stepSize, final DifferentialEquationInterface equation)
    {
        super(stepSize, equation);
    }

    @Override
    public double[] next(final double x, final double[] y)
    {
        double[] k1 = multiply(this.stepSize, this.equation.dy(x, y));
        double[] k2 = multiply(this.stepSize, this.equation.dy(x + 0.5d * this.stepSize, add(y, multiply(0.5d, k1))));
        double[] k3 = multiply(this.stepSize,
                this.equation.dy(x + 0.5d * this.stepSize, add(y, multiply(HM1SQRT2, k1), multiply(IMSQRT2D2, k2))));
        double[] k4 = multiply(this.stepSize,
                this.equation.dy(x + this.stepSize, add(y, multiply((-SQRT2D2), k2), multiply(IPSQRT2D2, k3))));
        double[] sum = add(k1, multiply(M2SQRT2, k2), multiply(P2SQRT2, k3), k4);
        return add(y, multiply(1.0 / 6.0d, sum));
    }
}
