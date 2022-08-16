package nl.tudelft.simulation.jstats.ode.integrators;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;

/**
 * The Adams-Bashforth-Moulton numerical estimator as described in <a href="https://mathworld.wolfram.com/AdamsMethod.html">
 * https://mathworld.wolfram.com/AdamsMethod.html </a>
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Adams extends CachingNumericalIntegrator
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new Adams integrator.
     * @param stepSize double; the stepSize to use in the estimation.
     * @param equation DifferentialEquationInterface; the equation to use.
     */
    public Adams(final double stepSize, final DifferentialEquationInterface equation)
    {
        this(stepSize, equation, NumericalIntegratorType.RUNGEKUTTA4, 10);
    }

    /**
     * constructs a new Adams integrator, indicating the starting method and number of substeps.
     * @param stepSize double; the stepSize to use in the estimation.
     * @param equation DifferentialEquationInterface; the equation to use.
     * @param primerIntegrationMethod NumericalIntegratorType; the primer integrator to use
     * @param startingSubSteps int; the number of substeps per timestep during starting of the integrator
     */
    public Adams(final double stepSize, final DifferentialEquationInterface equation,
            final NumericalIntegratorType primerIntegrationMethod, final int startingSubSteps)
    {
        super(stepSize, equation, 4, primerIntegrationMethod, startingSubSteps);
    }

    /** {@inheritDoc} */
    @Override
    public double[] next(final double x)
    {
        double[] y0 = getY(0);
        double[] dy0 = getDY(0);
        double[] dy1 = getDY(1);
        double[] dy2 = getDY(2);
        double[] dy3 = getDY(3);
        double[] sum = add(multiply(-9, dy3), multiply(37, dy2), multiply(-59, dy1), multiply(55, dy0));
        sum = multiply(this.stepSize / 24.0, sum);
        double[] p = add(y0, sum);
        sum = add(dy2, multiply(-5, dy1), multiply(19, dy0), multiply(9, this.equation.dy(x + this.stepSize, p)));
        return add(y0, multiply(this.stepSize / 24.0, sum));
    }
}
