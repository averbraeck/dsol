package nl.tudelft.simulation.dsol.experiment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.test.UnitTest;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * StreamInformationTest tests the StreamInformation object.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class StreamInformationTest
{
    /**
     * Test the StreamInformation object.
     */
    @Test
    public void testStreamInformation()
    {
        StreamInformation si = new StreamInformation();
        assertNotNull(si.getStreams());
        assertEquals(1, si.getStreams().size());
        si.addStream("stream", new MersenneTwister(112L));
        assertEquals(2, si.getStreams().size());
        assertNotNull(si.getStream("stream"));
        assertNotNull(si.getStream("default"));
        assertNull(si.getStream("stream2"));
        assertEquals(112L, si.getStream("stream").getSeed());
        StreamInformation si2 = new StreamInformation(new MersenneTwister(123L));
        assertEquals(123L, si2.getStream("default").getSeed());
        si.addStream("stream", new MersenneTwister(202L));
        assertEquals(2, si.getStreams().size());
        assertEquals(202L, si.getStream("stream").getSeed());

        UnitTest.testFail(() ->
        {
            new StreamInformation(null);
        });
        UnitTest.testFail(() ->
        {
            si.getStream(null);
        });
        UnitTest.testFail(() ->
        {
            si.addStream(null, new MersenneTwister(1L));
        });
        UnitTest.testFail(() ->
        {
            si.addStream("xyz", null);
        });
    }

    /**
     * Test the StreamSeedInformation object.
     */
    @Test
    public void testStreamSeedInformation()
    {
        StreamSeedInformation si = new StreamSeedInformation();
        assertNotNull(si.getStreams());
        assertEquals(1, si.getStreams().size());
        si.addStream("stream", new MersenneTwister(112L));
        assertEquals(2, si.getStreams().size());
        assertNotNull(si.getStream("stream"));
        assertNotNull(si.getStream("default"));
        assertNull(si.getStream("stream2"));
        assertEquals(112L, si.getStream("stream").getSeed());
        StreamSeedInformation si2 = new StreamSeedInformation(new MersenneTwister(123L));
        assertEquals(123L, si2.getStream("default").getSeed());
        si.addStream("stream", new MersenneTwister(202L));
        assertEquals(2, si.getStreams().size());
        assertEquals(202L, si.getStream("stream").getSeed());
        si.putSeedArray("default", new long[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100});
        assertNotNull(si.getSeedMap("default"));
        assertNull(si.getSeedMap("d"));
        assertNull(si.getSeedMap("stream"));
        assertEquals(10L, si.getSeedMap("default").get(0).longValue());

        // failures
        UnitTest.testFail(() ->
        {
            new StreamSeedInformation(null);
        });
        UnitTest.testFail(() ->
        {
            si.getStream(null);
        });
        UnitTest.testFail(() ->
        {
            si.addStream(null, new MersenneTwister(1L));
        });
        UnitTest.testFail(() ->
        {
            si.addStream("xyz", null);
        });
        UnitTest.testFail(() ->
        {
            si.getSeedMap(null);
        });
        UnitTest.testFail(() ->
        {
            si.putSeedArray(null, new long[] {1L, 2L});
        });
        UnitTest.testFail(() ->
        {
            si.putSeedList(null, new ArrayList<Long>());
        });
        UnitTest.testFail(() ->
        {
            si.putSeedMap(null, new LinkedHashMap<Integer, Long>());
        });
        UnitTest.testFail(() ->
        {
            si.putSeedArray("default", (long[]) null);
        });
        UnitTest.testFail(() ->
        {
            si.putSeedList("default", (List<Long>) null);
        });
        UnitTest.testFail(() ->
        {
            si.putSeedMap("default", (Map<Integer, Long>) null);
        });
        UnitTest.testFail(() ->
        {
            si.putSeedArray("d", new long[] {1L, 2L});
        });
        UnitTest.testFail(() ->
        {
            si.putSeedList("d", new ArrayList<Long>());
        });
        UnitTest.testFail(() ->
        {
            si.putSeedMap("d", new LinkedHashMap<Integer, Long>());
        });
    }

    /**
     * Test the StreamSeedUpdater object.
     */
    @Test
    public void testStreamSeedUpdater()
    {
        StreamSeedInformation si = new StreamSeedInformation();
        si.putSeedArray("default", new long[] {110L, 120L});
        StreamSeedUpdater ssu = new StreamSeedUpdater(si.getStreamSeedMap());
        assertEquals(1, ssu.getStreamSeedMap().size());
        assertEquals(110L, ssu.getStreamSeedMap().get("default").get(0).longValue());
        assertEquals(SimpleStreamUpdater.class, ssu.getFallbackStreamUpdater().getClass());
        assertEquals(10L, si.getStream("default").getSeed());
        ssu.updateSeed("default", si.getStream("default"), 0);
        assertEquals(110L, si.getStream("default").getSeed());
        ssu.updateSeed("default", si.getStream("default"), 1);
        assertEquals(120L, si.getStream("default").getSeed());
        ssu.updateSeed("default", si.getStream("default"), 2);
        assertNotEquals(110L, si.getStream("default").getSeed());
        assertNotEquals(120L, si.getStream("default").getSeed());
        assertNotEquals(10L, si.getStream("default").getSeed());
        ssu.updateSeed("xyz", si.getStream("default"), 2);

        // fallback
        ssu.setFallbackStreamUpdater(new StreamUpdater()
        {
            /** */
            private static final long serialVersionUID = 1L;

            @Override
            public void updateSeed(final String streamId, final StreamInterface stream, final int replicationNumber)
            {
                stream.setSeed(99L);
            }
        });
        ssu.updateSeed("default", si.getStream("default"), 0);
        assertEquals(110L, si.getStream("default").getSeed());
        ssu.updateSeed("default", si.getStream("default"), 1);
        assertEquals(120L, si.getStream("default").getSeed());
        ssu.updateSeed("default", si.getStream("default"), 2);
        assertEquals(99L, si.getStream("default").getSeed());
    }
}
