package nl.tudelft.simulation.jstats.ode.integrators;

import java.io.Serializable;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;

/**
 * Provides basic methods for all numerical integration methods. They mostly include matrix computation.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public abstract class NumericalIntegrator implements Serializable
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the step size to use. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double stepSize = Double.NaN;

    /** the calculated error of the last step. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double[] error = null;

    /** the equation to integrate. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DifferentialEquationInterface equation = null;

    /**
     * constructs a new NumericalIntegrator.
     * @param stepSize the stepSize
     * @param equation the differentialEquation
     */
    public NumericalIntegrator(final double stepSize, final DifferentialEquationInterface equation)
    {
        this.stepSize = stepSize;
        this.equation = equation;
    }

    /**
     * computes the next value.
     * @param x the x value corresponding to the last y-value computed
     * @param y the last y value
     * @return the new value
     */
    public abstract double[] next(double x, double[] y);

    /**
     * multiplies a vector with a constant.
     * @param constant the constant
     * @param vector the vector
     * @return the new vector
     */
    protected double[] multiply(final double constant, final double[] vector)
    {
        double[] prod = new double[vector.length];
        for (int i = 0; i < vector.length; i++)
        {
            prod[i] = constant * vector[i];
        }
        return prod;
    }

    /**
     * adds two vectors.
     * @param a vector a
     * @param b vector b
     * @return the new vector
     */
    protected double[] add(final double[] a, final double[] b)
    {
        double[] sum = new double[a.length];
        for (int i = 0; i < a.length; i++)
        {
            sum[i] = a[i] + b[i];
        }
        return sum;
    }

    /**
     * adds a number of vectors.
     * @param a vector a
     * @param b vector b
     * @param c vector c
     * @return the new vector
     */
    protected double[] add(final double[] a, final double[] b, final double[] c)
    {
        double[] sum = new double[a.length];
        for (int i = 0; i < a.length; i++)
        {
            sum[i] = a[i] + b[i] + c[i];
        }
        return sum;
    }

    /**
     * adds a number of vectors.
     * @param a vector a
     * @param b vector b
     * @param c vector c
     * @param d vector d
     * @return the sum
     */
    protected double[] add(final double[] a, final double[] b, final double[] c, final double[] d)
    {
        double[] sum = new double[a.length];
        for (int i = 0; i < a.length; i++)
        {
            sum[i] = a[i] + b[i] + c[i] + d[i];
        }
        return sum;
    }

    /**
     * adds a number of vectors.
     * @param a vector a
     * @param b vector b
     * @param c vector c
     * @param d vector d
     * @param e vector e
     * @return the sum
     */
    protected double[] add(final double[] a, final double[] b, final double[] c, final double[] d, final double[] e)
    {
        double[] sum = new double[a.length];
        for (int i = 0; i < a.length; i++)
        {
            sum[i] = a[i] + b[i] + c[i] + d[i] + e[i];
        }
        return sum;
    }

    /**
     * adds a number of vectors.
     * @param a vector a
     * @param b vector b
     * @param c vector c
     * @param d vector d
     * @param e vector e
     * @param f vector f
     * @return the sum
     */
    protected double[] add(final double[] a, final double[] b, final double[] c, final double[] d, final double[] e,
            final double[] f)
    {
        double[] sum = new double[a.length];
        for (int i = 0; i < a.length; i++)
        {
            sum[i] = a[i] + b[i] + c[i] + d[i] + e[i] + f[i];
        }
        return sum;
    }

    /**
     * @return Returns the current step size.
     */
    public double getStepSize()
    {
        return this.stepSize;
    }

    /**
     * @param stepSize The step size to set.
     */
    public void setStepSize(final double stepSize)
    {
        this.stepSize = stepSize;
    }

    /**
     * @return Returns the error.
     */
    public double[] getError()
    {
        return this.error;
    }

}
