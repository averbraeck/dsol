package nl.tudelft.simulation.dsol.serialize;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.formalisms.dess.DifferentialEquation;
import nl.tudelft.simulation.dsol.simulators.DessSimulatorInterface;
import nl.tudelft.simulation.jstats.ode.integrators.NumericalIntegratorType;

/**
 * The histogram specifies a histogram chart for the DSOL framework.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs"> Peter Jacobs </a>
 * @since 1.5
 */
public class SimpleDifferentialEquation extends DifferentialEquation
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new DifferentialEquation.
     * @param simulator the simulator
     * @param timeStep the time step
     * @param numericalIntegrator the integration method
     * @param numberOfVariables the number of variabes in the equation
     * @throws RemoteException on network error
     */
    public SimpleDifferentialEquation(final DessSimulatorInterface simulator, final double timeStep,
            final NumericalIntegratorType numericalIntegrator, final int numberOfVariables) throws RemoteException
    {
        super(simulator, timeStep, numericalIntegrator, numberOfVariables);
    }

    @Override
    public double[] dy(final double arg0, final double[] arg1)
    {
        return new double[] {1.0};
    }
}
