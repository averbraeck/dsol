package nl.tudelft.simulation.jstats.ode.integrators;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;
import nl.tudelft.simulation.language.DSOLRuntimeException;

/**
 * NumericalIntegratorType is an enum with the currently implemented integrators.
 * <p>
 * Copyright (c) 2019-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public enum NumericalIntegratorType
{
    /** Euler's integration. */
    EULER(0, Euler.class),

    /** Heun's integration. */
    HEUN(1, Heun.class),

    /** RungeKutta's (3rd level) integration. */
    RUNGEKUTTA3(2, RungeKutta3.class),

    /** RungeKutta's (4th level) integration. */
    RUNGEKUTTA4(3, RungeKutta4.class),

    /** Adam's integration. */
    ADAMS(4, Adams.class),

    /** Gill's integration. */
    GILL(5, Gill.class),

    /** Milne's integration. */
    MILNE(6, Milne.class),

    /** Runge-Kutta-Fehlberg integration. */
    RUNGEKUTTAFEHLBERG(7, RungeKuttaFehlberg.class),

    /** Runge-Kutta-Cash-Carp integration. */
    RUNGEKUTTACASHCARP(8, RungeKuttaCashCarp.class);

    /** the value from DSOL-1 before enum was introduced. */
    private final int value;

    /** the class of the integrator. */
    private final Class<? extends NumericalIntegrator> integratorClass;

    /**
     * Create a side; store the value from DSOL-1 as well.
     * @param value int; the value from DSOL-1 before enum was introduced
     * @param integratorClass Class&lt;? extends NumericalIntegrator&gt;; the class of the integrator
     */
    NumericalIntegratorType(final int value, final Class<? extends NumericalIntegrator> integratorClass)
    {
        this.value = value;
        this.integratorClass = integratorClass;
    }

    /**
     * Returns the value from DSOL-1 before enum was introduced.
     * @return int; the value from DSOL-1 before enum was introduced
     */
    public int getValue()
    {
        return this.value;
    }

    /**
     * Get the integrator class belonging to the name.
     * @return Class&lt;? extends NumericalIntegrator&gt;; the class of the integrator
     */
    public Class<? extends NumericalIntegrator> getIntegratorClass()
    {
        return this.integratorClass;
    }

    /**
     * Get an instance of the integrator.
     * @param stepSize double; the starting step size to use
     * @param equation DifferentialEquationInterface; the differential equation
     * @return the integrator
     */
    public NumericalIntegrator getInstance(final double stepSize, final DifferentialEquationInterface equation)
    {
        try
        {
            Constructor<? extends NumericalIntegrator> constructor =
                    this.integratorClass.getConstructor(double.class, DifferentialEquationInterface.class);
            return constructor.newInstance(stepSize, equation);
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e)
        {
            throw new DSOLRuntimeException(e);
        }
    }
}
