package nl.tudelft.simulation.examples.dsol.animation.continuous;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.formalisms.dess.DifferentialEquation;
import nl.tudelft.simulation.dsol.simulators.DessSimulatorInterface;

/**
 * A Positioner.
 * <p>
 * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://simulation.tudelft.nl/people/jacobs.html">Peter Jacobs </a>
 */
public class Positioner extends DifferentialEquation<Double>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new Positioner.
     * @param simulator DessSimulatorInterface&lt;Double&gt;; the simulator
     * @throws RemoteException on remote error
     */
    public Positioner(final DessSimulatorInterface<Double> simulator) throws RemoteException
    {
        super(simulator, 2);
        this.initialize(0.0, new double[] {0.0, 0.0});
    }

    /**
     * sets the value.
     * @param value double; the new value
     */
    public void setValue(final double value)
    {
        super.initialize(this.simulator.getSimulatorTime(), new double[] {0.0, value});
    }

    /** {@inheritDoc} */
    @Override
    public double[] dy(final double x, final double[] y)
    {
        double[] dy = new double[2];
        dy[0] = 0.5; // a(t) = constant
        dy[1] = y[0]; // v(t) = a(t)
        return dy;
    }
}
