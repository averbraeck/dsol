package nl.tudelft.simulation.dsol.experiment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

/**
 * RunControlTest tests the RunControl object.
 * <p>
 * Copyright (c) 2021-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RunControlTest
{
    /**
     * test the RunControl object.
     */
    @Test
    public void testRunControl()
    {
        RunControl<Double> rcdg = new RunControl<>("rcdg", 10.0, 5.0, 20.0);
        assertEquals("rcdg", rcdg.getId());
        assertEquals("rcdg", rcdg.getDescription());
        rcdg.setDescription("description");
        assertEquals("description", rcdg.getDescription());
        assertEquals(Double.valueOf(10.0), rcdg.getStartTime());
        assertEquals(Double.valueOf(30.0), rcdg.getEndTime());
        assertEquals(Double.valueOf(15.0), rcdg.getWarmupTime());
        assertEquals(10.0, rcdg.getStartTime(), 1E-6);
        assertEquals(30.0, rcdg.getEndTime(), 1E-6);
        assertEquals(15.0, rcdg.getWarmupTime(), 1E-6);
        assertEquals(20.0, rcdg.getRunLength(), 1E-6);
        assertEquals(5.0, rcdg.getWarmupPeriod(), 1E-6);

        // types
        RunControl<Double> rcd = new RunControl<Double>("rc", 10.0, 5.0, 20.0);
        assertEquals(30.0, rcd.getEndTime(), 1E-6);
        RunControl<Float> rcf = new RunControl<Float>("rc", 10.0f, 5.0f, 20.0f);
        assertEquals(30.0f, rcf.getEndTime(), 1E-6);
        RunControl<Long> rcl = new RunControl<Long>("rc", 10L, 5L, 20L);
        assertEquals(30L, rcl.getEndTime().longValue());
        RunControl<Duration> rcdu = new RunControl<Duration>("rc", new Duration(10.0, DurationUnit.HOUR),
                new Duration(5.0, DurationUnit.HOUR), new Duration(20.0, DurationUnit.HOUR));
        assertEquals(30.0, rcdu.getEndTime().getInUnit(), 1E-6);
        RunControl<FloatDuration> rcdf = new RunControl<FloatDuration>("rc", new FloatDuration(10.0f, DurationUnit.HOUR),
                new FloatDuration(5.0f, DurationUnit.HOUR), new FloatDuration(20.0f, DurationUnit.HOUR));
        assertEquals(30.0f, rcdf.getEndTime().getInUnit(), 1E-6);

        // equals and hashCode
        assertTrue(rcd.equals(rcd));
        assertNotEquals(rcd, rcf);
        assertTrue(rcd.hashCode() == rcd.hashCode());
        assertFalse(rcd.hashCode() == rcf.hashCode());
        assertNotEquals(rcd, null);
        assertNotEquals(rcd, new RunControl<Double>("rc2", 10.0, 5.0, 20.0));
        assertNotEquals(rcd, new RunControl<Double>("rc", 11.0, 5.0, 20.0));
        assertNotEquals(rcd, new RunControl<Double>("rc", 10.0, 15.0, 20.0));
        assertNotEquals(rcd, new RunControl<Double>("rc", 10.0, 5.0, 21.0));
        assertNotEquals(rcd, new Object());
        assertEquals(rcd, new RunControl<Double>("rc", 10.0, 5.0, 20.0));
        assertTrue(rcd.toString().contains("rc"));

        // errors
        Try.testFail(() ->
        {
            new RunControl<Duration>(null, new Duration(10.0, DurationUnit.HOUR), new Duration(5.0, DurationUnit.DAY),
                    new Duration(20.0, DurationUnit.HOUR));
        });
        Try.testFail(() ->
        { new RunControl<Duration>("rc", null, new Duration(5.0, DurationUnit.DAY), new Duration(20.0, DurationUnit.HOUR)); });
        Try.testFail(() ->
        {
            new RunControl<Duration>("rc", new Duration(10.0, DurationUnit.HOUR), null, new Duration(20.0, DurationUnit.HOUR));
        });
        Try.testFail(() ->
        { new RunControl<Duration>("rc", new Duration(10.0, DurationUnit.HOUR), new Duration(5.0, DurationUnit.DAY), null); });
        Try.testFail(() ->
        { new RunControl<Double>("rc", 10.0, 5.0, 5.0); });
        Try.testFail(() ->
        { new RunControl<Double>("rc", 10.0, -1.0, 15.0); });
        Try.testFail(() ->
        { new RunControl<Double>("rc", 10.0, 5.0, -15.0); });
        Try.testFail(() ->
        { new RunControl<Double>("rc", 10.0, 5.0, 0.0); });
    }

    /**
     * test the ExperimentRunControl object.
     */
    @Test
    public void testExperimentRunControl()
    {
        ExperimentRunControl<Double> rcdg = new ExperimentRunControl<>("rcdg", 10.0, 5.0, 20.0, 10);
        assertEquals("rcdg", rcdg.getId());
        assertEquals("rcdg", rcdg.getDescription());
        rcdg.setDescription("description");
        assertEquals("description", rcdg.getDescription());
        assertEquals(Double.valueOf(10.0), rcdg.getStartTime());
        assertEquals(Double.valueOf(30.0), rcdg.getEndTime());
        assertEquals(Double.valueOf(15.0), rcdg.getWarmupTime());
        assertEquals(10.0, rcdg.getStartTime(), 1E-6);
        assertEquals(30.0, rcdg.getEndTime(), 1E-6);
        assertEquals(15.0, rcdg.getWarmupTime(), 1E-6);
        assertEquals(20.0, rcdg.getRunLength(), 1E-6);
        assertEquals(5.0, rcdg.getWarmupPeriod(), 1E-6);
        assertEquals(10, rcdg.getNumberOfReplications());

        // types
        ExperimentRunControl<Double> rcd = new ExperimentRunControl<Double>("rc", 10.0, 5.0, 20.0, 10);
        assertEquals(30.0, rcd.getEndTime(), 1E-6);
        ExperimentRunControl<Float> rcf = new ExperimentRunControl<Float>("rc", 10.0f, 5.0f, 20.0f, 10);
        assertEquals(30.0f, rcf.getEndTime(), 1E-6);
        ExperimentRunControl<Long> rcl = new ExperimentRunControl<Long>("rc", 10L, 5L, 20L, 10);
        assertEquals(30L, rcl.getEndTime().longValue());
        ExperimentRunControl<Duration> rcdu = new ExperimentRunControl<Duration>("rc", new Duration(10.0, DurationUnit.HOUR),
                new Duration(5.0, DurationUnit.HOUR), new Duration(20.0, DurationUnit.HOUR), 10);
        assertEquals(30.0, rcdu.getEndTime().getInUnit(), 1E-6);
        ExperimentRunControl<FloatDuration> rcdf =
                new ExperimentRunControl<FloatDuration>("rc",
                        new FloatDuration(10.0f, DurationUnit.HOUR),
                        new FloatDuration(5.0f, DurationUnit.HOUR), new FloatDuration(20.0f, DurationUnit.HOUR), 10);
        assertEquals(30.0f, rcdf.getEndTime().getInUnit(), 1E-6);

        // equals and hashCode
        assertTrue(rcd.equals(rcd));
        assertNotEquals(rcd, rcf);
        assertTrue(rcd.hashCode() == rcd.hashCode());
        assertFalse(rcd.hashCode() == rcf.hashCode());
        assertNotEquals(rcd, null);
        assertNotEquals(rcd, new ExperimentRunControl<Double>("rc2", 10.0, 5.0, 20.0, 10));
        assertNotEquals(rcd, new ExperimentRunControl<Double>("rc", 11.0, 5.0, 20.0, 10));
        assertNotEquals(rcd, new ExperimentRunControl<Double>("rc", 10.0, 15.0, 20.0, 10));
        assertNotEquals(rcd, new ExperimentRunControl<Double>("rc", 10.0, 5.0, 21.0, 10));
        assertNotEquals(rcd, new ExperimentRunControl<Double>("rc", 10.0, 5.0, 20.0, 11));
        assertNotEquals(rcd, new Object());
        assertEquals(rcd, new ExperimentRunControl<Double>("rc", 10.0, 5.0, 20.0, 10));
        assertTrue(rcd.toString().contains("rc"));

        // errors
        Try.testFail(() ->
        {
            new ExperimentRunControl<Duration>(null, new Duration(10.0, DurationUnit.HOUR), new Duration(5.0, DurationUnit.DAY),
                    new Duration(20.0, DurationUnit.HOUR), 10);
        });
        Try.testFail(() ->
        {
            new ExperimentRunControl<Duration>("rc", null, new Duration(5.0, DurationUnit.DAY),
                    new Duration(20.0, DurationUnit.HOUR), 10);
        });
        Try.testFail(() ->
        {
            new ExperimentRunControl<Duration>("rc", new Duration(10.0, DurationUnit.HOUR), null,
                    new Duration(20.0, DurationUnit.HOUR), 10);
        });
        Try.testFail(() ->
        {
            new ExperimentRunControl<Duration>("rc", new Duration(10.0, DurationUnit.HOUR), new Duration(5.0, DurationUnit.DAY),
                    null, 10);
        });
        Try.testFail(() ->
        { new ExperimentRunControl<Double>("rc", 10.0, 5.0, 5.0, 10); });
        Try.testFail(() ->
        { new ExperimentRunControl<Double>("rc", 10.0, -1.0, 15.0, 10); });
        Try.testFail(() ->
        { new ExperimentRunControl<Double>("rc", 10.0, 5.0, -15.0, 10); });
        Try.testFail(() ->
        { new ExperimentRunControl<Double>("rc", 10.0, 5.0, 0.0, 10); });
        Try.testFail(() ->
        { new ExperimentRunControl<Double>("rc", 10.0, 5.0, 15.0, 0); });
        Try.testFail(() ->
        { new ExperimentRunControl<Double>("rc", 10.0, 5.0, 15.0, -1); });
    }

}
