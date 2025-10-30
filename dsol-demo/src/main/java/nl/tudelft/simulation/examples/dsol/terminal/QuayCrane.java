package nl.tudelft.simulation.examples.dsol.terminal;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * The QCs modeled as resources.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class QuayCrane extends IntResource<Double>
{
    /** QC time delay. */
    private final DistContinuous qcTime;

    /**
     * @param simulator the simulator
     * @param description the description
     * @param capacity the capacity
     * @param qcTime QC time delay
     */
    public QuayCrane(final DevsSimulatorInterface<Double> simulator, final String description, final long capacity,
            final DistContinuous qcTime)
    {
        super(simulator, description, capacity);
        this.qcTime = qcTime;
    }

    /**
     * @return the QC handling time
     */
    public double drawDelay()
    {
        return this.qcTime.draw();
    }
}
