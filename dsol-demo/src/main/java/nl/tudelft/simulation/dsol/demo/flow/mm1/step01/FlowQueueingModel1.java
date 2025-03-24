package nl.tudelft.simulation.dsol.demo.flow.mm1.step01;

import nl.tudelft.simulation.dsol.formalisms.flow.Create;
import nl.tudelft.simulation.dsol.formalisms.flow.Delay;
import nl.tudelft.simulation.dsol.formalisms.flow.Destroy;
import nl.tudelft.simulation.dsol.formalisms.flow.Entity;
import nl.tudelft.simulation.dsol.formalisms.flow.Release;
import nl.tudelft.simulation.dsol.formalisms.flow.Resource;
import nl.tudelft.simulation.dsol.formalisms.flow.Seize;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * An M/M/1 model based on flow simulation building blocks.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FlowQueueingModel1 extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the distribution of the interarrival time between entities. */
    private final DistContinuousSimulationTime<Double> interArrivalTime;

    /** the distribution of the processing time of entities. */
    private final DistContinuousSimulationTime<Double> processingTime;

    /** entity counter. */
    protected int entityNumber = 0;

    /**
     * Constructor for the model. This provides the distribution functions and the simulator to the model.
     * @param simulator the simulator on which to schedule new events
     * @param interArrivalTime the distribution of the interarrival time between entities
     * @param processingTime the distribution of the processing time of entities
     */
    public FlowQueueingModel1(final DevsSimulatorInterface<Double> simulator, final DistContinuous interArrivalTime,
            final DistContinuous processingTime)
    {
        super(simulator);
        this.interArrivalTime = new DistContinuousSimulationTime.TimeDouble(interArrivalTime);
        this.processingTime = new DistContinuousSimulationTime.TimeDouble(processingTime);
    }

    @Override
    public void constructModel()
    {
        // create entities
        var generator = new Create<Double>("create", getSimulator());
        generator.setIntervalDist(this.interArrivalTime);
        generator.setEntitySupplier(() -> {
            var entity = new Entity<Double>(String.valueOf(++FlowQueueingModel1.this.entityNumber), getSimulator());
            System.out.println(
                    String.format("Time: %.2f  Entity %s generated", getSimulator().getSimulatorTime(), entity.getId()));
            return entity;
        });

        // the resource
        var resource = new Resource.DoubleCapacity<Double>("resource", getSimulator(), 1.0);

        // seize the resource
        var seize = new Seize.DoubleCapacity<Double>("seize", getSimulator(), resource);
        seize.setFixedCapacityClaim(1.0);
        generator.setDestination(seize);

        // delay for the processing time
        var delay = new Delay<Double>("delay", getSimulator()).setDelayDistribution(this.processingTime);
        seize.setDestination(delay);

        // release the resource
        var release = new Release<Double>("release", getSimulator(), resource);
        delay.setDestination(release);

        // depart from the model
        var destroy = new Destroy<Double>("destroy", getSimulator())
        {
            private static final long serialVersionUID = 1L;

            @Override
            public synchronized void receiveEntity(final Entity<Double> entity)
            {
                System.out.println(String.format("Time: %.2f  Entity %s left the model", getSimulator().getSimulatorTime(),
                        entity.getId()));
                super.receiveEntity(entity);
            }
        };
        release.setDestination(destroy);
    }

}
