package nl.tudelft.simulation.dsol.formalisms.dess;

import java.rmi.RemoteException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventType;
import org.djutils.event.reference.ReferenceType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.simulators.DessSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.ode.DifferentialEquationInterface;
import nl.tudelft.simulation.jstats.ode.integrators.NumericalIntegratorType;

/**
 * The Differential equation provides a reference implementation of the differential equation.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the time type
 * @since 1.5
 */
public abstract class DifferentialEquation<T extends Number & Comparable<T>>
        extends nl.tudelft.simulation.jstats.ode.DifferentialEquation implements DifferentialEquationInterface, EventListener
{
    /** */
    private static final long serialVersionUID = 20140804L;

    /** VALUE_CHANGED_EVENT is fired on value changes. The array is initialized in the ODE's constructor. */
    @SuppressWarnings({"checkstyle:visibilitymodifier", "checkstyle:membername"})
    public EventType[] VALUE_CHANGED_EVENT;

    /** FUNCTION_CHANGED_EVENT is fired on function changes. */
    @SuppressWarnings({"checkstyle:visibilitymodifier", "checkstyle:membername"})
    public EventType FUNCTION_CHANGED_EVENT = new EventType("FUNCTION_CHANGED_EVENT", MetaData.NO_META_DATA);

    /** simulator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DessSimulatorInterface<T> simulator = null;

    /** the number of variables in the equation. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public int numberOfVariables;

    /** the previousX. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double previousX;

    /** the previousY. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double[] previousY = null;

    /**
     * Construct a new DifferentialEquation with a step size equal to the simulator time step, and Runge-Kutta4 as the default
     * integrator. Indicate the number of variables that the differential qquation will use.
     * @param simulator the simulator
     * @param numberOfVariables the number of variables in the equation
     * @throws RemoteException on remote network exception for the listener
     */
    public DifferentialEquation(final DessSimulatorInterface<T> simulator, final int numberOfVariables) throws RemoteException
    {
        this(simulator, simulator.getTimeStep().doubleValue(), NumericalIntegratorType.RUNGEKUTTA4, numberOfVariables);
    }

    /**
     * constructs a new DifferentialEquation with a step size equal to the simulator timestep.
     * @param simulator the simulator
     * @param numericalIntegrator the actual integrator to be used.
     * @param numberOfVariables the number of variables in the equation
     * @throws RemoteException on remote network exception for the listener
     */
    public DifferentialEquation(final DessSimulatorInterface<T> simulator, final NumericalIntegratorType numericalIntegrator,
            final int numberOfVariables) throws RemoteException
    {
        this(simulator, simulator.getTimeStep().doubleValue(), numericalIntegrator, numberOfVariables);
    }

    /**
     * constructs a new DifferentialEquation.
     * @param simulator the simulator.
     * @param timeStep the timeStep for ODE estimation.
     * @param numericalIntegrator the actual integrator to be used.
     * @param numberOfVariables the number of variables in the equation
     * @throws RemoteException on remote network exception for the listener
     */
    public DifferentialEquation(final DessSimulatorInterface<T> simulator, final double timeStep,
            final NumericalIntegratorType numericalIntegrator, final int numberOfVariables) throws RemoteException
    {
        super(timeStep, numericalIntegrator);
        this.simulator = simulator;
        this.numberOfVariables = numberOfVariables;
        this.VALUE_CHANGED_EVENT = new EventType[this.numberOfVariables];
        for (int i = 0; i < this.numberOfVariables; i++)
        {
            this.VALUE_CHANGED_EVENT[i] =
                    new EventType(new MetaData("VALUE_CHANGED_EVENT[" + i + "]", "value changed for variable " + i,
                            new ObjectDescriptor("value_" + i, "value for variable " + i, Double.class)));
        }
        simulator.addListener(this, SimulatorInterface.TIME_CHANGED_EVENT, ReferenceType.STRONG);
    }

    @Override
    public synchronized void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(SimulatorInterface.TIME_CHANGED_EVENT))
        {
            if (this.simulator.getSimulatorTime().doubleValue() < super.lastX || Double.isNaN(super.lastX))
            { return; }
            // do not put super here!
            this.previousY = integrateY(this.simulator.getSimulatorTime().doubleValue(), this.previousX, this.previousY);
            for (int i = 0; i < super.lastY.length; i++)
            {
                this.fireUnverifiedTimedEvent(this.VALUE_CHANGED_EVENT[i], this.previousY[i],
                        this.simulator.getSimulatorTime());
            }
            this.previousX = this.simulator.getSimulatorTime().doubleValue();
        }
    }

    @Override
    public void initialize(final double x, final double[] y)
    {
        super.initialize(x, y);
        this.previousX = x;
        this.previousY = y;
    }
}
