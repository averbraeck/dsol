package nl.tudelft.simulation.jstats.ode.integrators;

import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;

/**
 * The CachingNumericalIntegrator is the basis for an integrator that needs access to previously calculated values of y', e.g.
 * y'_(k-1), y'_(k-2), etc.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public abstract class CachingNumericalIntegrator extends NumericalIntegrator
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the number of cachePlaces to store, e.g for k-1, k-2 set it to 2. */
    private int cachePlaces = 0;

    /** the cache for y(k-1), y(k-2), etc. */
    private double[][] cacheY;

    /** the cache for y'(k-1), y'(k-2), etc. */
    private double[][] cacheDY;

    /** the number of cache places filled = the last cache place used. */
    private int lastCachePlace = -1;

    /** The primer integrator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected NumericalIntegrator startingIntegrator = null;

    /** the substeps to use when starting the integrator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected int startingSubSteps = 10;

    /**
     * constructs a new CachingNumericalIntegrator with a fixed number of cache places.
     * @param stepSize double; the stepSize
     * @param equation DifferentialEquationInterface; the differentialEquation
     * @param cachePlaces int; the number of cache places to store
     * @param primerIntegrationMethod NumericalIntegratorType; the primer integrator to use
     * @param startingSubSteps int; the number of sub-steps per stepSize during starting of the integrator
     */
    public CachingNumericalIntegrator(final double stepSize, final DifferentialEquationInterface equation,
            final int cachePlaces, final NumericalIntegratorType primerIntegrationMethod, final int startingSubSteps)
    {
        super(stepSize, equation);
        this.cachePlaces = cachePlaces;
        this.cacheY = new double[cachePlaces][];
        this.cacheDY = new double[cachePlaces][];
        this.startingIntegrator = primerIntegrationMethod.getInstance(stepSize / (1.0d * startingSubSteps), equation);
        this.startingSubSteps = startingSubSteps;
    }

    /** {@inheritDoc} */
    @Override
    public void setStepSize(final double stepSize)
    {
        super.setStepSize(stepSize);
        this.lastCachePlace = -1;
    }

    /** {@inheritDoc} */
    @Override
    public double[] next(final double x, final double[] y)
    {
        double[] ynext = null;
        // look whether we have to prime, or can calculate
        if (this.lastCachePlace < this.cachePlaces)
        {
            // calculate next y-value using the primer, which can have a
            // much smaller timestep
            ynext = y.clone();
            double xstep = x;
            for (int i = 0; i < this.startingSubSteps; i++)
            {
                ynext = this.startingIntegrator.next(xstep, ynext);
                xstep += this.stepSize / (1.0d * this.startingSubSteps);
            }
        }
        else
        {
            // calculate next y-value using the intended method
            ynext = next(x);
        }
        this.lastCachePlace++;
        this.cacheY[this.lastCachePlace % this.cachePlaces] = ynext;
        this.cacheDY[this.lastCachePlace % this.cachePlaces] = this.equation.dy(x + this.stepSize, ynext);
        return ynext;
    }

    /**
     * get a cached Y-value.
     * @param numberDown int; the number of the previous value we want
     * @return the corresponding Y-value
     */
    public double[] getY(final int numberDown)
    {
        if (this.lastCachePlace < this.cachePlaces)
        {
            throw new RuntimeException("Tried to retrieve y-value that was not yet primed");
        }
        if (numberDown >= this.cachePlaces)
        {
            throw new RuntimeException("Tried to retrieve y-value beyond cache limits");
        }
        return this.cacheY[(this.lastCachePlace - numberDown) % this.cachePlaces].clone();
    }

    /**
     * get a cached dY-value.
     * @param numberDown int; the number of the previous value we want
     * @return the corresponding dY-value
     */
    public double[] getDY(final int numberDown)
    {
        if (this.lastCachePlace < this.cachePlaces)
        {
            throw new RuntimeException("Tried to retrieve dy-value that was not yet primed");
        }
        if (numberDown >= this.cachePlaces)
        {
            throw new RuntimeException("Tried to retrieve dy-value beyond cache limits");
        }
        return this.cacheDY[(this.lastCachePlace - numberDown) % this.cachePlaces].clone();
    }

    /**
     * The integrators that extend the CachingNumericalIntegrator calculate the value of y(x+stepSize) just based on the
     * x-value. They retrieve y(x), y(x-stepSize), etc. or y(k), y(k-1) all from the cache.
     * @param x double; the x-value to use in the calculation
     * @return the value of y(x+stepSize)
     */
    public abstract double[] next(double x);
}
