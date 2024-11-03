package nl.tudelft.simulation.examples.dsol.terminal;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * The AGVs modeled as resources.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Agv extends IntResource<Double>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** AGV time delay. */
    private final DistContinuous agvTime;

    /**
     * @param simulator DevsSimulatorInterface&lt;Double&gt;; the simulator
     * @param description String; the description
     * @param capacity long; the capacity
     * @param agvTime DistContinuous; AGV time delay
     */
    public Agv(final DevsSimulatorInterface<Double> simulator, final String description, final long capacity,
            final DistContinuous agvTime)
    {
        super(simulator, description, capacity);
        this.agvTime = agvTime;
    }

    /**
     * @return the AGV handling time
     */
    public double drawDelay()
    {
        return this.agvTime.draw();
    }
}
