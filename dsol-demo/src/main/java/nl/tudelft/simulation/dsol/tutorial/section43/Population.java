package nl.tudelft.simulation.dsol.tutorial.section43;

import nl.tudelft.simulation.dsol.formalisms.dess.DifferentialEquation;
import nl.tudelft.simulation.dsol.simulators.DessSimulatorInterface;
import nl.tudelft.simulation.jstats.ode.integrators.NumericalIntegratorType;

/**
 * The population differential equation.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class Population extends DifferentialEquation<Double>
{
    /** Lotka-Volterra parameters. */
    private double a;

    /** Lotka-Volterra parameters. */
    private double b;

    /** Lotka-Volterra parameters. */
    private double c;

    /** Lotka-Volterra parameters. */
    private double d;

    /**
     * constructs a new Population.
     * @param simulator the simulator
     */
    public Population(final DessSimulatorInterface<Double> simulator)
    {
        super(simulator, simulator.getTimeStep(), NumericalIntegratorType.ADAMS, 2);
        double predator = 10;
        double prey = 20;
        this.initialize(0.0, new double[] {predator, prey});
        this.a = 1;
        this.b = 0.1;
        this.c = 1;
        this.d = 0.2;
    }

    @Override
    public double[] dy(final double time, final double[] y)
    {
        double[] dy = new double[2];
        dy[0] = -this.a * y[0] + this.b * y[0] * y[1];
        dy[1] = this.c * y[1] - this.d * y[1] * y[0];
        return dy;
    }
}
