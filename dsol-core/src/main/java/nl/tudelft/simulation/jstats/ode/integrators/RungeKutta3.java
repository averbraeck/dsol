package nl.tudelft.simulation.jstats.ode.integrators;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;

/**
 * The RungeKutta 3 numerical integrator.
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
public class RungeKutta3 extends NumericalIntegrator
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new RungeKutta3.
     * @param stepSize double; the stepSize
     * @param equation DifferentialEquationInterface; the differentialEquation
     */
    public RungeKutta3(final double stepSize, final DifferentialEquationInterface equation)
    {
        super(stepSize, equation);
    }

    /** {@inheritDoc} */
    @Override
    public double[] next(final double x, final double[] y)
    {
        double[] k1 = this.equation.dy(x, y);
        double[] k2 = this.equation.dy(x + 0.5 * this.stepSize, add(y, multiply(0.5 * this.stepSize, k1)));
        double[] k3 = this.equation.dy(x + 0.5 * this.stepSize, add(y, multiply(0.5 * this.stepSize, k2)));
        double[] sum = add(k1, multiply(4.0, k2), k3);
        return add(y, multiply(this.stepSize / 6.0, sum));
    }
}
