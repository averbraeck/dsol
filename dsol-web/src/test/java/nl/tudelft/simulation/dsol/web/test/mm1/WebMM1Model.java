package nl.tudelft.simulation.dsol.web.test.mm1;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.Resource;
import nl.tudelft.simulation.dsol.formalisms.flow.Create;
import nl.tudelft.simulation.dsol.formalisms.flow.Delay;
import nl.tudelft.simulation.dsol.formalisms.flow.Entity;
import nl.tudelft.simulation.dsol.formalisms.flow.FlowObject;
import nl.tudelft.simulation.dsol.formalisms.flow.Release;
import nl.tudelft.simulation.dsol.formalisms.flow.Seize;
import nl.tudelft.simulation.dsol.formalisms.flow.statistics.Utilization;
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
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The M/M/1 example as published in Simulation Modeling and Analysis by A.M. Law &amp; W.D. Kelton section 1.4 and 2.4.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class WebMM1Model extends AbstractDsolModel<Double, DevsSimulator<Double>>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** tally dN. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimTally<Double> dN;

    /** tally qN. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimPersistent<Double> qN;

    /** utilization uN. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    Utilization<Double> uN;

    /**
     * @param simulator DevsSimulator&lt;Double&gt;; the simulator to use for this model
     */
    public WebMM1Model(final DevsSimulator<Double> simulator)
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

    /** {@inheritDoc} */
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
            Create<Double> generator = new Create<Double>("generate", this.simulator,
                    new DistContinuousSimulationTime.TimeDouble(avgStartTime == 0.0
                            ? new DistConstant(defaultStream, avgStartTime) : new DistExponential(defaultStream, avgStartTime)),
                    new DistContinuousSimulationTime.TimeDouble(new DistExponential(defaultStream, avgInterArrivalTime)),
                    batchSize)
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected Entity<Double> generateEntity()
                {
                    return new Entity<Double>("entity", getSimulator().getSimulatorTime());
                }
            };

            // The queue, the resource and the release
            Resource<Double> resource = new Resource<>("resource", this.simulator, capacity);

            // created the caiming and releasing of the resource
            FlowObject<Double> queue = new Seize<Double>("Seize", this.simulator, resource);
            FlowObject<Double> release = new Release<Double>("Release", this.simulator, resource, 1.0);

            // The server
            DistContinuousSimulationTime<Double> serviceTime =
                    new DistContinuousSimulationTime.TimeDouble(new DistExponential(defaultStream, avgServiceTime));
            FlowObject<Double> server = new Delay<Double>("Delay", this.simulator, serviceTime);

            // The flow
            generator.setDestination(queue);
            queue.setDestination(server);
            server.setDestination(release);

            // Statistics
            this.dN = new SimTally<Double>("d(n)", this, queue, Seize.DELAY_TIME);
            this.qN = new SimPersistent<Double>("q(n)", this, queue, Seize.QUEUE_LENGTH_EVENT);
            this.uN = new Utilization<>("u(n)", this, server);

        }
        catch (InputParameterException e)
        {
            throw new SimRuntimeException(e);
        }
    }
}
