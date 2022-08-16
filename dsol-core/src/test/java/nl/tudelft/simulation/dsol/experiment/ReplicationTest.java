package nl.tudelft.simulation.dsol.experiment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.djutils.exceptions.Try;
import org.junit.Test;

/**
 * ReplicationTest tests the SingleReplication and AbstractReplication objects.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ReplicationTest
{
    /**
     * Test the SingleReplication.
     */
    @Test
    @SuppressWarnings("null")
    public void testSingleReplication()
    {
        SingleReplication<Double> srd = new SingleReplication<Double>("srd", 10.0, 1.0, 12.0);
        assertEquals("srd", srd.getId());
        assertEquals("srd", srd.getDescription());
        srd.setDescription("single replication");
        assertEquals("single replication", srd.getDescription());
        assertEquals(10.0, srd.getStartTime(), 1E-9);
        assertEquals(10.0, srd.getStartSimTime(), 1E-9);
        assertEquals(22.0, srd.getEndTime(), 1E-9);
        assertEquals(22.0, srd.getEndSimTime(), 1E-9);
        assertEquals(11.0, srd.getWarmupTime(), 1E-9);
        assertEquals(11.0, srd.getWarmupSimTime(), 1E-9);
        assertEquals(12.0, srd.getRunLength(), 1E-9);
        assertEquals(1.0, srd.getWarmupPeriod(), 1E-9);
        assertTrue(srd.toString().contains("single replication"));
        assertNotNull(srd.getContext());

        // errors
        Try.testFail(() ->
        { new SingleReplication<Double>(null, 10.0, 1.0, 12.0); });
        Try.testFail(() ->
        { new SingleReplication<Double>("x", (Double) null, 1.0, 12.0); });
        Try.testFail(() ->
        { new SingleReplication<Double>("x", 10.0, (Double) null, 12.0); });
        Try.testFail(() ->
        { new SingleReplication<Double>("x", 10.0, 1.0, (Double) null); });
        new SingleReplication<Double>("xyz1", 10.0, 0.0, 20.0);
        new SingleReplication<Double>("xyz2", -10.0, 0.0, 20.0);
        Try.testFail(() ->
        { new SingleReplication<Double>("xyz", 10.0, -1.0, 20.0); });
        Try.testFail(() ->
        { new SingleReplication<Double>("xyz", 10.0, 1.0, 0.0); });
        Try.testFail(() ->
        { new SingleReplication<Double>("xyz", 10.0, 1.0, -20.0); });
        Try.testFail(() ->
        { new SingleReplication<Double>("xyz", 10.0, 10.0, 5.0); });
        Try.testFail(() ->
        { new SingleReplication<Double>("xyz", 10.0, 10.0, 10.0); });

        // other types
        SingleReplication<Float> srf = new SingleReplication<Float>("srf", 10.0f, 1.0f, 12.0f);
        assertEquals(22.0f, srf.getEndTime(), 1E-6);
        SingleReplication<Long> srl = new SingleReplication<Long>("srl", 10L, 1L, 12L);
        assertEquals(22L, srl.getEndTime().longValue());
        SingleReplication<Duration> srdu = new SingleReplication<Duration>("srdu", new Duration(10.0, DurationUnit.HOUR),
                Duration.ZERO, new Duration(12.0, DurationUnit.HOUR));
        assertEquals(new Duration(22.0, DurationUnit.HOUR).si, srdu.getEndTime().si, 1E-6);
        SingleReplication<FloatDuration> srfu = new SingleReplication<FloatDuration>("srfu",
                new FloatDuration(10.0f, DurationUnit.HOUR), FloatDuration.ZERO, new FloatDuration(12.0f, DurationUnit.HOUR));
        assertEquals(new FloatDuration(22.0f, DurationUnit.HOUR).si, srfu.getEndTime().si, 1E-6);

        // Generic type
        SingleReplication<Double> sr = new SingleReplication<>("srd", new Double(10.0), 1.0, 12.0);
        assertEquals(10.0, sr.getStartTime(), 1E-9);
        assertEquals(10.0, sr.getStartSimTime(), 1E-9);
        assertEquals(22.0, sr.getEndTime(), 1E-9);
        assertEquals(22.0, sr.getEndSimTime(), 1E-9);
        assertEquals(11.0, sr.getWarmupTime(), 1E-9);
        assertEquals(11.0, sr.getWarmupSimTime(), 1E-9);
        assertEquals(12.0, sr.getRunLength(), 1E-9);
        assertEquals(1.0, sr.getWarmupPeriod(), 1E-9);

        // remove from context
        srd.removeFromContext();
    }
}
