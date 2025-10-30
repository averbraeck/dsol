package nl.tudelft.simulation.dsol.simulators.clock;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.dsol.simulators.DevsAnimator;

/**
 * ClockDevsAnimator is an extension of the DevsAnimator that works with Duration and ClockTime.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class ClockDevsAnimator extends DevsAnimator<Duration> implements ClockDevsSimulatorInterface
{
    /** The absolute startTime of the simulator. */
    private final ClockTime startTime;

    /**
     * Instantiate a ClockDevsAnimator.
     * @param id the id of the simulator
     * @param startTime the startTime of the simulator as a ClockTime
     */
    public ClockDevsAnimator(final Serializable id, final ClockTime startTime)
    {
        super(id);
        this.startTime = startTime;
    }

    @Override
    public ClockTime getStartClockTime()
    {
        return this.startTime;
    }

}
