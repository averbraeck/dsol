package nl.tudelft.simulation.examples.dsol.dess;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.formalisms.dess.DifferentialEquation;
import nl.tudelft.simulation.dsol.simulators.DessSimulatorInterface;

/**
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <p>
 * See for project information <a href="https://simulation.tudelft.nl/" target="_blank"> www.simulation.tudelft.nl</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Distance extends DifferentialEquation<Double>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the speed. */
    private Speed speed = null;

    /**
     * constructs a new Distance.
     * @param simulator DessSimulatorInterface&lt;Double&gt;; the simulator
     * @throws RemoteException on network error
     */
    public Distance(final DessSimulatorInterface<Double> simulator) throws RemoteException
    {
        super(simulator, 1);
        this.speed = new Speed(simulator);
        this.speed.initialize(10, new double[] {0});
        this.initialize(10, new double[] {0});
    }

    @Override
    public double[] dy(final double x, final double[] y)
    {
        return this.speed.y(x);
    }
}
