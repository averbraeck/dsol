package nl.tudelft.simulation.dsol.logger;

import java.util.function.Function;

import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * SimTimeSupplier formats the simulation time as a String.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the simulation time type
 */
public class SimTimeFormatter<T extends Number & Comparable<T>> implements Function<T, String>
{
    @Override
    public String apply(final T simTime)
    {
        return SimTime.format(simTime);
    }
}
