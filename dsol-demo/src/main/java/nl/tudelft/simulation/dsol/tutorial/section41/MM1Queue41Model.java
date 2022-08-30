package nl.tudelft.simulation.dsol.tutorial.section41;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.Resource;
import nl.tudelft.simulation.dsol.formalisms.flow.Delay;
import nl.tudelft.simulation.dsol.formalisms.flow.Generator;
import nl.tudelft.simulation.dsol.formalisms.flow.Release;
import nl.tudelft.simulation.dsol.formalisms.flow.Seize;
import nl.tudelft.simulation.dsol.formalisms.flow.StationInterface;
import nl.tudelft.simulation.dsol.formalisms.flow.statistics.Utilization;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulator;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.SimTally;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The M/M/1 example as published in Simulation Modeling and Analysis by A.M. Law &amp; W.D. Kelton section 1.4 and 2.4.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class MM1Queue41Model extends AbstractDSOLModel<Double, DEVSSimulator<Double>>
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
    Utilization<Double> uN;

    /**
     * @param simulator DEVSSimulator&lt;Double&gt;; the simulator
     */
    public MM1Queue41Model(final DEVSSimulator<Double> simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        StreamInterface defaultStream = new MersenneTwister(2L);

        // The Generator
        Generator<Double> generator = new Generator<Double>("Generator", this.simulator, Object.class, null);
        generator.setInterval(new DistContinuousSimulationTime.TimeDouble(new DistExponential(defaultStream, 1.0)));
        generator.setStartTime(new DistContinuousSimulationTime.TimeDouble(new DistConstant(defaultStream, 0.0)));
        generator.setBatchSize(new DistDiscreteConstant(defaultStream, 1));

        // The queue, the resource and the release
        Resource<Double> resource = new Resource<>(this.simulator, 1.0);

        // created a resource
        StationInterface<Double> queue = new Seize<Double>("Seize", this.simulator, resource);
        StationInterface<Double> release = new Release<Double>("Release", this.simulator, resource, 1.0);

        // The server
        DistContinuousSimulationTime<Double> serviceTime =
                new DistContinuousSimulationTime.TimeDouble(new DistExponential(defaultStream, 0.5));
        StationInterface<Double> server = new Delay<Double>("Delay", this.simulator, serviceTime);

        // The flow
        generator.setDestination(queue);
        queue.setDestination(server);
        server.setDestination(release);

        // Statistics
        try
        {
            this.dN = new SimTally<Double>("d(n)", this.simulator, queue, Seize.DELAY_TIME);
            this.qN = new SimPersistent<Double>("q(n)", this.simulator, queue, Seize.QUEUE_LENGTH_EVENT);
            this.uN = new Utilization<>("u(n)", this.simulator, server);
        }
        catch (Exception exception)
        {
            throw new SimRuntimeException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "MM1Queue41Model";
    }
}
