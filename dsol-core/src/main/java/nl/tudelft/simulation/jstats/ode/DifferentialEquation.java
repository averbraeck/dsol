package nl.tudelft.simulation.jstats.ode;

import org.djutils.event.EventProducer;

import nl.tudelft.simulation.jstats.ode.integrators.NumericalIntegrator;
import nl.tudelft.simulation.jstats.ode.integrators.NumericalIntegratorType;

/**
 * The DifferentialEquation is the abstract basis for the DESS formalism.
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
public abstract class DifferentialEquation extends EventProducer implements DifferentialEquationInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the numerical integrator for the differential equations. */
    private NumericalIntegrator integrator = null;

    /** the last calculated value array for lastX, initialized with the initial value array y0. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double[] lastY = null;

    /** the stepSize; can be negative or positive. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double stepSize = Double.NaN;

    /** the last x value, initialized with x0 to start integration. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double lastX = Double.NaN;

    /**
     * constructs a new DifferentialEquation with a user-specified integrator.
     * @param stepSize double; the stepSize to use.
     * @param integratorType NumericalIntegratorType; the integrator to use.
     */
    public DifferentialEquation(final double stepSize, final NumericalIntegratorType integratorType)
    {
        super();
        this.stepSize = stepSize;
        this.integrator = integratorType.getInstance(stepSize, this);
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(final double x0, final double[] y0)
    {
        this.lastX = x0;
        this.lastY = y0;
    }

    /** {@inheritDoc} */
    @Override
    public double[] y(final double x)
    {
        // If the ODE is not initialized, the cache is empty.
        if (Double.isNaN(this.lastX))
        {
            throw new RuntimeException("differential equation not initialized");
        }

        if (x == this.lastX)
        {
            return this.lastY;
        }

        // Are we integrating in the right direction?
        if (Math.signum(this.stepSize) != Math.signum(x - this.lastX))
        {
            throw new RuntimeException("Sign of the stepsize does not integrate towards x from x0");
        }
        return this.integrateY(x, this.lastX, this.lastY);
    }

    /**
     * integrates Y.
     * @param x double; the x-value
     * @param initialX double; the initial X value, non-final (will be updated)
     * @param initialY double[]; the initial Y value, non-final (will be updated)
     * @return the new Y value
     */
    @SuppressWarnings("checkstyle:finalparameters")
    protected double[] integrateY(final double x, /* non-final */ double initialX, /* non-final */ double[] initialY)
    {
        // we request the new value from the integrator.
        if (this.stepSize > 0)
        {
            while (x > initialX + this.stepSize)
            {
                initialY = this.integrator.next(initialX, initialY); // XXX: this process can adapt the stepSize (!)
                initialX = initialX + this.stepSize;
            }
        }
        else // negative stepsize!
        {
            while (x < initialX + this.stepSize)
            {
                initialY = this.integrator.next(initialX, initialY); // XXX: this process can adapt the stepSize (!)
                initialX = initialX + this.stepSize;
            }
        }

        // We are in our final step. // XXX don't understand
        double[] nextValue = this.integrator.next(initialX, initialY);
        double ratio = (x - initialX) / this.stepSize;
        for (int i = 0; i < initialY.length; i++)
        {
            initialY[i] = initialY[i] + ratio * (nextValue[i] - initialY[i]);
        }
        this.lastX = x;
        this.lastY = initialY;
        return initialY;
    }

    /**
     * @return Returns the integrator.
     */
    public NumericalIntegrator getIntegrator()
    {
        return this.integrator;
    }

    /**
     * @param integrator NumericalIntegrator; The integrator to set.
     */
    public void setIntegrator(final NumericalIntegrator integrator)
    {
        this.integrator = integrator;
    }

}
