package nl.tudelft.simulation.dsol.experiment;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * SimpleStreamUpdater updates the streams based on the hashCode of the name of the stream and the replication number.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SimpleStreamUpdater implements StreamUpdater
{
    /** */
    private static final long serialVersionUID = 20210405L;

    /** {@inheritDoc} */
    @Override
    public void updateSeed(final String streamId, final StreamInterface stream, final int replicationNumber)
    {
        stream.setSeed(stream.getOriginalSeed() + replicationNumber * (1_000_037L + streamId.hashCode()));
    }

}
