package nl.tudelft.simulation.examples.dsol.dess;

import nl.tudelft.simulation.dsol.formalisms.dess.DifferentialEquation;
import nl.tudelft.simulation.dsol.simulators.DessSimulatorInterface;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <p>
 * See for project information <a href="https://simulation.tudelft.nl/" target="_blank"> www.simulation.tudelft.nl</a>.
 * <p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Speed extends DifferentialEquation<Double>
{
    /**
     * constructs a new Speed.
     * @param simulator the simulator
     */
    public Speed(final DessSimulatorInterface<Double> simulator)
    {
        super(simulator, 1);
    }

    @Override
    public double[] dy(final double x, final double[] y)
    {
        return new double[] {0.5};
    }
}
