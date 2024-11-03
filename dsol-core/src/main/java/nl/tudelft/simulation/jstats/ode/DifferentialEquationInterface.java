package nl.tudelft.simulation.jstats.ode;

/**
 * An interface for the DifferentialEquation.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public interface DifferentialEquationInterface
{
    /**
     * initializes the differential equation.
     * @param x0 double; the initial x-value
     * @param y0 double[]; the initial y-value array
     */
    void initialize(double x0, double[] y0);

    /**
     * returns y as a function of x.
     * @param x double; the x-value
     * @return y
     */
    double[] y(double x);

    /**
     * returns dy as a function of x,y.
     * @param x double; the x-value
     * @param y double[]; the y-value
     * @return dy/dx as a function of x,y
     */
    double[] dy(double x, double[] y);
}
