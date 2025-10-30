package nl.tudelft.simulation.jstats.ode.integrators;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;

/**
 * The Euler numerical estimator as described in <a href="https://mathworld.wolfram.com/EulerForwardMethod.html">
 * https://mathworld.wolfram.com/EulerForwardMethod.html </a>
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Euler extends NumericalIntegrator
{
    /**
     * constructs a new Euler.
     * @param stepSize the stepSize
     * @param equation the differentialEquation
     */
    public Euler(final double stepSize, final DifferentialEquationInterface equation)
    {
        super(stepSize, equation);
    }

    @Override
    public double[] next(final double x, final double[] y)
    {
        return add(y, multiply(this.stepSize, this.equation.dy(x, y)));
    }
}
