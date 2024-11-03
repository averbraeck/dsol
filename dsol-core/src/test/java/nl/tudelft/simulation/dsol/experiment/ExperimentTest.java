package nl.tudelft.simulation.dsol.experiment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.Sleep;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimCounter;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.SimTally;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.naming.context.ContextInterface;

/**
 * ExperimentTest tests the correct working of the Experiment object.
 * <p>
 * Copyright (c) 2021-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class ExperimentTest
{
    /**
     * Test the Experiment object.
     */
    @Test
    public void testDoubleExperiment()
    {
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("simulator");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = new CountModel(simulator, new LinkedHashMap<>());
        Experiment<Double, DevsSimulatorInterface<Double>> expd =
                new Experiment<>("Exp 1", simulator, model, 10.0, 1.0, 12.0, 10);

        expd.removeFromContext(); // should not yet have been added
        assertEquals("Exp 1", expd.getId());
        assertEquals("Exp 1", expd.getDescription());
        expd.setDescription("Experiment 1");
        assertEquals("Experiment 1", expd.getDescription());
        assertEquals(10.0, expd.getStartTime(), 1E-9);
        assertEquals(10.0, expd.getStartTime(), 1E-9);
        assertEquals(22.0, expd.getEndTime(), 1E-9);
        assertEquals(22.0, expd.getEndTime(), 1E-9);
        assertEquals(11.0, expd.getWarmupTime(), 1E-9);
        assertEquals(11.0, expd.getWarmupTime(), 1E-9);
        assertEquals(12.0, expd.getRunLength(), 1E-9);
        assertEquals(1.0, expd.getWarmupPeriod(), 1E-9);
        assertNotNull(expd.getStreamUpdater());
        assertTrue(expd.toString().contains("Experiment 1"));
        ContextInterface c1 = expd.getContext();
        assertNotNull(c1);
        ContextInterface c2 = expd.getContext();
        assertEquals(c1, c2);
        assertEquals(simulator, expd.getSimulator());
        assertEquals(model, expd.getModel());
        assertEquals(10, expd.getNumberOfReplications());
        assertEquals(0, expd.getStartedReplications().size());
        assertEquals(-1, expd.getCurrentReplication());
        expd.removeFromContext();

        // errors
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>(null, simulator, model, 0.0, 10.0, 20.0, 10); });
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>("exp1", null, model, 0.0, 10.0, 20.0, 10); });
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>("exp1", simulator, null, 0.0, 10.0, 20.0, 10); });
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>("exp1", simulator, model, (Double) null, 10.0, 20.0, 10); });
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>("exp1", simulator, model, 0.0, (Double) null, 20.0, 10); });
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>("exp1", simulator, model, 0.0, 10.0, (Double) null, 10); });
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>("exp1", simulator, model, 0.0, -10.0, 20.0, 10); });
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>("exp1", simulator, model, 0.0, 10.0, 0.0, 10); });
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>("exp1", simulator, model, 0.0, 10.0, -20.0, 10); });
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>("exp1", simulator, model, 0.0, 10.0, 20.0, 0); });
        Try.testFail(() ->
        { new Experiment<Double, DevsSimulatorInterface<Double>>("exp1", simulator, model, 0.0, 10.0, 20.0, -10); });

        // should be ok
        new Experiment<Double, DevsSimulatorInterface<Double>>("Exp1a", simulator, model, 0.0, 0.0, 20.0, 10);
        new Experiment<Double, DevsSimulatorInterface<Double>>("Exp1a", simulator, model, -10.0, 0.0, 20.0, 10);
    }

    /**
     * Test the Experiment object.
     */
    @Test
    public void testExperimentTypes()
    {
        // generic experiment
        DevsSimulator<Double> simd = new DevsSimulator<>("simulator");
        DsolModel<Double, DevsSimulatorInterface<Double>> modd =
                new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simd)
                {
                    /** */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void constructModel() throws SimRuntimeException
                    {
                        //
                    }
                };
        Experiment<Double, DevsSimulatorInterface<Double>> d1 = new Experiment<>("Exp 1", simd, modd, 10.0, 1.0, 12.0, 10);
        assertEquals(22.0, d1.getEndTime(), 1E-6);
        assertEquals(modd, d1.getModel());
        d1.makeExperimentReplication();

        // float experiment
        DevsSimulator<Float> simf = new DevsSimulator<Float>("simulator");
        DsolModel<Float, DevsSimulatorInterface<Float>> modf = new AbstractDsolModel<Float, DevsSimulatorInterface<Float>>(simf)
        {

            /** */
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                //
            }
        };
        Experiment<Float, DevsSimulatorInterface<Float>> f1 =
                new Experiment<Float, DevsSimulatorInterface<Float>>("f1", simf, modf, 10.0f, 1.0f, 12.0f, 10);
        assertEquals(22.0f, f1.getEndTime(), 1E-6);
        assertEquals(modf, f1.getModel());
        f1.makeExperimentReplication();

        // long experiment
        DevsSimulator<Long> siml = new DevsSimulator<Long>("simulator");
        DsolModel<Long, DevsSimulatorInterface<Long>> modl = new AbstractDsolModel<Long, DevsSimulatorInterface<Long>>(siml)
        {

            /** */
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                //
            }
        };
        Experiment<Long, DevsSimulatorInterface<Long>> l1 =
                new Experiment<Long, DevsSimulatorInterface<Long>>("l1", siml, modl, 10L, 1L, 12L, 10);
        assertEquals(22L, l1.getEndTime().longValue());
        assertEquals(modl, l1.getModel());
        l1.makeExperimentReplication();

        // double unit experiment
        DevsSimulator<Duration> simdu = new DevsSimulator<Duration>("simulator");
        DsolModel<Duration, DevsSimulatorInterface<Duration>> moddu =
                new AbstractDsolModel<Duration, DevsSimulatorInterface<Duration>>(simdu)
                {

                    /** */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void constructModel() throws SimRuntimeException
                    {
                        //
                    }
                };
        Experiment<Duration, DevsSimulatorInterface<Duration>> du1 = new Experiment<Duration, DevsSimulatorInterface<Duration>>(
                "du1", simdu, moddu, Duration.ZERO, Duration.ZERO, Duration.instantiateSI(1000.0), 10);
        assertEquals(1000.0, du1.getEndTime().doubleValue(), 1E-6);
        assertEquals(moddu, du1.getModel());
        du1.makeExperimentReplication();

        // float unit experiment
        DevsSimulator<FloatDuration> simfu = new DevsSimulator<FloatDuration>("simulator");
        DsolModel<FloatDuration, DevsSimulatorInterface<FloatDuration>> modfu =
                new AbstractDsolModel<FloatDuration, DevsSimulatorInterface<FloatDuration>>(simfu)
                {

                    /** */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void constructModel() throws SimRuntimeException
                    {
                        //
                    }
                };
        Experiment<FloatDuration, DevsSimulatorInterface<FloatDuration>> fu1 = new Experiment<>("du1", simfu, modfu,
                FloatDuration.ZERO, FloatDuration.ZERO, FloatDuration.instantiateSI(1000.0f), 10);
        assertEquals(1000.0f, fu1.getEndTime().floatValue(), 1E-6);
        assertEquals(modfu, fu1.getModel());
        fu1.makeExperimentReplication();
    }

    /**
     * test the execution of an experiment with 10 replications.
     * @throws RemoteException on error
     */
    @Test
    public void testExperimentRun() throws RemoteException
    {
        SortedMap<Integer, Integer> dataCollector = new TreeMap<>();
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("simulator");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = new CountModel(simulator, dataCollector);
        Experiment<Double, DevsSimulatorInterface<Double>> expd =
                new Experiment<Double, DevsSimulatorInterface<Double>>("Exp 1", simulator, model, 10.0, 1.0, 12.0, 10);

        expd.start();
        int count = 0;
        while (expd.isRunning() && count < 20000)
        {
            count++;
            Sleep.sleep(1);
        }
        assertTrue(count < 20000);

        assertEquals(10, dataCollector.size());
        for (int i = 0; i < 10; i++)
        {
            assertEquals(13, dataCollector.get(i).intValue());
        }

        // test failure
        Try.testFail(() -> expd.start());
        Try.testFail(() -> expd.startNextReplication());

        // reset and do again
        dataCollector.clear();
        expd.reset();
        expd.start();
        count = 0;
        while (expd.isRunning() && count < 20000)
        {
            count++;
            Sleep.sleep(1);
        }
        assertTrue(count < 20000);
        assertEquals(10, dataCollector.size());
        for (int i : dataCollector.keySet())
        {
            assertEquals(13, dataCollector.get(i).intValue());
        }
    }

    /**
     * test the execution of an experiment with 10 replications and the use of a seed updater.
     * @throws RemoteException on error
     */
    @Test
    public void testExperimentRunSeedUpdater() throws RemoteException
    {
        SortedMap<Integer, Integer> dataCollector = new TreeMap<>();
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("simulator");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = new CountModel(simulator, dataCollector);
        StreamSeedInformation streamInformation = new StreamSeedInformation();
        streamInformation.addStream("default", new MersenneTwister(10L));
        streamInformation.addStream("iatStream", new MersenneTwister(20L));
        streamInformation.addStream("procStream", new MersenneTwister(30L));
        streamInformation.putSeedArray("iatStream", new long[] {1, 2, 3, 4, 5, 6, 7, 8});
        List<Long> seedList = List.of(10L, 20L, 30L, 40L, 50L, 60L, 70L, 80L, 90L, 100L);
        streamInformation.putSeedList("procStream", seedList);
        Map<Integer, Long> seedMap = new LinkedHashMap<>();
        seedMap.put(0, 100L);
        seedMap.put(1, 200L);
        streamInformation.putSeedMap("default", seedMap);
        model.setStreamInformation(streamInformation);
        Experiment<Double, DevsSimulatorInterface<Double>> expd =
                new Experiment<Double, DevsSimulatorInterface<Double>>("Exp 1", simulator, model, 10.0, 1.0, 12.0, 10);
        expd.setStreamUpdater(new StreamSeedUpdater(streamInformation.getStreamSeedMap()));

        expd.start();
        int count = 0;
        while (expd.isRunning() && count < 20000)
        {
            count++;
            Sleep.sleep(1);
        }
        assertTrue(count < 20000);

        assertEquals(10, dataCollector.size());
        for (int i = 0; i < 10; i++)
        {
            assertEquals(13, dataCollector.get(i).intValue());
        }
    }

    /**
     * test the calculation of a summary statistic foran experiment with 10 replications.
     * @throws RemoteException on error
     */
    @Test
    public void testSummaryStatistics() throws RemoteException
    {
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("simulator");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = new MM1Model(simulator);
        Experiment<Double, DevsSimulatorInterface<Double>> expd =
                new Experiment<Double, DevsSimulatorInterface<Double>>("Exp 1", simulator, model, 10.0, 10.0, 20.0, 10);

        expd.start();
        int count = 0;
        while (expd.isRunning() && count < 25000)
        {
            count++;
            Sleep.sleep(1);
        }
        assertTrue(count < 25000);

        assertEquals(3, expd.getSummaryStatistics().size());
        System.out.println(expd.getSummaryStatistics());
    }

    /**
     * Model class.
     */
    public static class CountModel extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** counter. */
        private int count;

        /** the id of this model. */
        private int id = -1;

        /** the data collector. */
        private final Map<Integer, Integer> dataCollector;

        /**
         * @param simulator the simulator
         * @param dataCollector the data collector
         */
        public CountModel(final DevsSimulatorInterface<Double> simulator, final Map<Integer, Integer> dataCollector)
        {
            super(simulator);
            this.dataCollector = dataCollector;
        }

        @Override
        public void constructModel() throws SimRuntimeException
        {
            this.count = 0;
            this.id++;
            next();
        }

        /** next method. */
        public void next()
        {
            getSimulator().scheduleEventRel(1.0, this, "next", null);
            this.count++;
            this.dataCollector.put(this.id, this.count);
        }
    }

    /**
     * Quick and dirty MM1 queuing system Model class.
     */
    public static class MM1Model extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** queue. */
        private List<Entity> queue;

        /** generator: every 1 time unit on average. */
        private DistContinuous iatDist;

        /** processing : 0.8 time units on average. */
        private DistContinuous procDist;

        /** a counter of the number of arrivals. */
        private SimCounter<Double> count;

        /** a tally of the waiting time. */
        private SimTally<Double> queueTimeTally;

        /** a persistent of the time in queue. */
        private SimPersistent<Double> nrInQueuePersistent;

        /**
         * @param simulator the simulator
         */
        public MM1Model(final DevsSimulatorInterface<Double> simulator)
        {
            super(simulator);
            this.iatDist = new DistExponential(getStream("default"), 1.0);
            this.procDist = new DistExponential(getStream("default"), 0.8);
        }

        /**
         * @param simulator the simulator
         * @param streamInformation the streams to use
         */
        public MM1Model(final DevsSimulatorInterface<Double> simulator, final StreamInformation streamInformation)
        {
            super(simulator, streamInformation);
            this.iatDist = new DistExponential(getStream("iatStream"), 1.0);
            this.procDist = new DistExponential(getStream("procStream"), 0.8);
        }

        @Override
        public void constructModel() throws SimRuntimeException
        {
            this.queue = new ArrayList<>();
            this.outputStatistics.clear();
            this.count = new SimCounter<Double>("arrivals", this);
            this.count.initialize();
            this.outputStatistics.add(this.count);
            this.queueTimeTally = new SimTally<Double>("timeInQueue", this);
            this.queueTimeTally.initialize();
            this.outputStatistics.add(this.queueTimeTally);
            this.nrInQueuePersistent = new SimPersistent<Double>("nrInQueue", this);
            this.nrInQueuePersistent.initialize();
            this.outputStatistics.add(this.nrInQueuePersistent);
            next();
        }

        /** next method. */
        public void next()
        {
            this.count.register(1);
            Entity entity = new Entity(this.simulator.getSimulatorTime());
            this.queue.add(entity);
            this.nrInQueuePersistent.register(10.0, this.queue.size());
            getSimulator().scheduleEventRel(this.iatDist.draw(), this, "next", null);
            getSimulator().scheduleEventRel(this.procDist.draw(), this, "endWait", new Object[] {entity});
        }

        /** @param entity the entity that is ready */
        protected void endWait(final Entity entity)
        {
            this.queue.remove(entity);
            this.nrInQueuePersistent.register(10.0, this.queue.size());
            this.queueTimeTally.register(this.simulator.getSimulatorTime() - entity.getCreateTime());
        }
    }

    /** the entity class. */
    static class Entity
    {
        /** the of creation of the entity. */
        private final double createTime;

        /**
         * @param createTime the create time of the entity.
         */
        Entity(final double createTime)
        {
            this.createTime = createTime;
        }

        /** @return the createTime of the entity. */
        double getCreateTime()
        {
            return this.createTime;
        }
    }
}
