package nl.tudelft.simulation.jstats.ode.integrators;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;

/**
 * The Milne numerical estimator as described in <a href="https://mathworld.wolfram.com/MilnesMethod.html">
 * https://mathworld.wolfram.com/MilnesMethod.html </a>
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
public class Milne extends CachingNumericalIntegrator
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new Milne integrator.
     * @param stepSize double; the stepSize to use in the estimation.
     * @param equation DifferentialEquationInterface; the equation to use.
     */
    public Milne(final double stepSize, final DifferentialEquationInterface equation)
    {
        super(stepSize, equation, 4, NumericalIntegratorType.RUNGEKUTTA4, 10);
    }

    /**
     * constructs a new Milne integrator, indicating the starting method and number of substeps.
     * @param stepSize double; the stepSize to use in the estimation.
     * @param equation DifferentialEquationInterface; the equation to use.
     * @param primerIntegrationMethod NumericalIntegratorType; the primer integrator to use
     * @param startingSubSteps int; the number of substeps per timestep during starting of the integrator
     */
    public Milne(final double stepSize, final DifferentialEquationInterface equation,
            final NumericalIntegratorType primerIntegrationMethod, final int startingSubSteps)
    {
        super(stepSize, equation, 4, primerIntegrationMethod, startingSubSteps);
    }

    /** {@inheritDoc} */
    @Override
    public double[] next(final double x)
    {
        double[] y3 = getY(3);
        double[] y1 = getY(1);
        double[] dy2 = getDY(2);
        double[] dy1 = getDY(1);
        double[] dy0 = getDY(0);

        // Let's evaluate the predictor
        double[] p =
                add(y3, multiply(4 * this.stepSize / 3.0, add(multiply(2.0, dy0), multiply(-1.0, dy1), multiply(2.0, dy2))));

        // Now we compute the corrector
        return add(y1, multiply(this.stepSize / 3.0,
                add(multiply(1.0, dy1), multiply(4.0, dy0), this.equation.dy(x + this.stepSize, p))));
    }
}
