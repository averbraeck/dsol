package nl.tudelft.simulation.examples.dsol.terminal;

import nl.tudelft.simulation.dsol.simulators.DevsxSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * The QCs modeled as resources.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class QC extends IntResource<Double>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** QC time delay. */
    private final DistContinuous qcTime;

    /**
     * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator
     * @param description String; the description
     * @param capacity long; the capacity
     * @param qcTime DistContinuous; QC time delay
     */
    public QC(final DevsxSimulatorInterface<Double> simulator, final String description, final long capacity,
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
