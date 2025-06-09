package nl.tudelft.simulation.dsol.demo.flow.mm1;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.flow.Create;
import nl.tudelft.simulation.dsol.formalisms.flow.Delay;
import nl.tudelft.simulation.dsol.formalisms.flow.Entity;
import nl.tudelft.simulation.dsol.formalisms.flow.Release;
import nl.tudelft.simulation.dsol.formalisms.flow.Resource;
import nl.tudelft.simulation.dsol.formalisms.flow.Seize;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterInteger;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.SimTally;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The M/M/1 example as published in Simulation Modeling and Analysis by A.M. Law &amp; W.D. Kelton section 1.4 and 2.4.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class MM1Model extends AbstractDsolModel<Double, DevsSimulator<Double>>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** tally dN. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimTally<Double> dN;

    /** persistent qN. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimPersistent<Double> qN;

    /** utilization uN. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimPersistent<Double> uN;

    /**
     * @param simulator the simulator
     */
    public MM1Model(final DevsSimulator<Double> simulator)
    {
        super(simulator);

        try
        {
            InputParameterMap generatorMap = new InputParameterMap("generator", "Generator", "Generator", 1.0);
            generatorMap
                    .add(new InputParameterDouble("intervalTime", "Average interval time", "Average interval time", 1.0, 1.0));
            generatorMap.add(new InputParameterDouble("startTime", "Generator start time", "Generator start time", 0.0, 2.0));
            generatorMap.add(new InputParameterInteger("batchSize", "Batch size", "batch size", 1, 3.0));
            this.inputParameterMap.add(generatorMap);
            InputParameterMap resourceMap = new InputParameterMap("resource", "Resource", "Resource", 2.0);
            resourceMap.add(new InputParameterInteger("capacity", "Resource capacity", "Resource capacity", 1, 1.0));
            resourceMap.add(new InputParameterDouble("serviceTime", "Average service time", "Average service time", 0.9, 2.0));
            this.inputParameterMap.add(resourceMap);
        }
        catch (Exception e)
        {
            throw new SimRuntimeException(e);
        }
    }

    @Override
    public void constructModel() throws SimRuntimeException
    {
        StreamInterface defaultStream = getDefaultStream();

        try
        {
            int capacity = (Integer) getInputParameter("resource.capacity");
            int batchSize = (Integer) getInputParameter("generator.batchSize");
            double avgStartTime = (Double) getInputParameter("generator.startTime");
            double avgInterArrivalTime = (Double) getInputParameter("generator.intervalTime");
            double avgServiceTime = (Double) getInputParameter("resource.serviceTime");

            // The Generator
            Create<Double> generator = new Create<Double>("generate", this.simulator)
                    .setStartTimeDist(new DistContinuousSimulationTime.TimeDouble(avgStartTime == 0.0
                            ? new DistConstant(defaultStream, avgStartTime) : new DistExponential(defaultStream, avgStartTime)))
                    .setIntervalDist(new DistContinuousSimulationTime.TimeDouble(
                            new DistExponential(defaultStream, avgInterArrivalTime)))
                    .setBatchSizeDist(new DistDiscreteConstant(defaultStream, batchSize))
                    .setEntitySupplier(() -> new Entity<Double>("entity", getSimulator()));

            // The queue, the resource and the release
            var resource = new Resource.DoubleCapacity<>("resource", this.simulator, capacity);
            resource.setDefaultStatistics();

            // created the claiming of the resource
            var seize = new Seize.DoubleCapacity<Double>("Seize", this.simulator, resource);
            seize.setFixedCapacityClaim(1.0);
            seize.setDefaultStatistics();

            // The delay for the service
            DistContinuousSimulationTime<Double> serviceTime =
                    new DistContinuousSimulationTime.TimeDouble(new DistExponential(defaultStream, avgServiceTime));
            var service = new Delay<Double>("Delay", this.simulator).setDelayDistribution(serviceTime);

            // release the claimed resource
            var release = new Release.DoubleCapacity<Double>("Release", this.simulator, resource);
            release.setFixedCapacityRelease(1.0);

            // The flow
            generator.setDestination(seize);
            seize.setDestination(service);
            service.setDestination(release);

            // Statistics
            this.dN = seize.getStorageTimeStatistic();
            this.qN = seize.getNumberStoredStatistic();
            this.uN = resource.getUtilizationStatistic();

        }
        catch (InputParameterException e)
        {
            throw new SimRuntimeException(e);
        }
    }
}
