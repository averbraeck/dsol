package nl.tudelft.simulation.jstats.ode.integrators;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;

/**
 * The RungeKutta4 numerical integrator.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class RungeKutta4 extends NumericalIntegrator
{
    /**
     * constructs a new RungeKutta4.
     * @param stepSize the stepSize
     * @param equation the differentialEquation
     */
    public RungeKutta4(final double stepSize, final DifferentialEquationInterface equation)
    {
        super(stepSize, equation);
    }

    @Override
    public double[] next(final double x, final double[] y)
    {
        double[] k1 = this.equation.dy(x, y);
        double[] k2 = this.equation.dy(x + 0.5 * this.stepSize, add(y, multiply(0.5 * this.stepSize, k1)));
        double[] k3 = this.equation.dy(x + 0.5 * this.stepSize, add(y, multiply(0.5 * this.stepSize, k2)));
        double[] k4 = this.equation.dy(x + this.stepSize, add(y, multiply(this.stepSize, k3)));
        double[] sum = add(k1, multiply(2.0, k2), multiply(2.0, k3), k4);
        return add(y, multiply(this.stepSize / 6.0, sum));
    }
}
