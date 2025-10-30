package nl.tudelft.simulation.jstats.ode.integrators;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;

/**
 * The Heun numerical estimator.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Heun extends NumericalIntegrator
{
    /**
     * constructs a new Heun.
     * @param stepSize the stepSize
     * @param equation the equation
     */
    public Heun(final double stepSize, final DifferentialEquationInterface equation)
    {
        super(stepSize, equation);
    }

    @Override
    public double[] next(final double x, final double[] y)
    {
        double[] f0 = this.equation.dy(x, y);
        double[] p = add(y, multiply(this.stepSize, f0));
        return add(y, multiply(0.5 * this.stepSize, add(f0, this.equation.dy(x + this.stepSize, p))));
    }
}
