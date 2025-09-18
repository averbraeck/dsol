package nl.tudelft.simulation.dsol.simulators.clock;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;

/**
 * ClockDevsRealTimeAnimator is an extension of the DevsRealTimeAnimator that works with Duration and ClockTime.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class ClockDevsRealTimeAnimator extends DevsRealTimeAnimator<Duration> implements ClockDevsSimulatorInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The absolute startTime of the simulator. */
    private final ClockTime startTime;

    /**
     * Instantiate a ClockDevsRealTimeAnimator.
     * @param id the id of the simulator
     * @param startTime the startTime of the simulator as a ClockTime
     */
    public ClockDevsRealTimeAnimator(final Serializable id, final ClockTime startTime)
    {
        super(id);
        this.startTime = startTime;
    }

    @Override
    public ClockTime getStartClockTime()
    {
        return this.startTime;
    }

    @Override
    public Duration simulatorTimeForWallClockMillis(final double wallMilliseconds)
    {
        return Duration.ofSI(wallMilliseconds / 1000.0);
    }

}
