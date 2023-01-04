package nl.tudelft.simulation.dsol.tutorial.section42;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * A Warehouse.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Warehouse implements SellerInterface
{
    /** simulator. the simulator to schedule on. */
    private DEVSSimulatorInterface<Double> simulator = null;

    /** the delivery or leadTime. */
    private DistContinuous leadTime = null;

    /**
     * constructs a new Warehouse.
     * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator to schedule on
     */
    public Warehouse(final DEVSSimulatorInterface<Double> simulator)
    {
        super();
        this.simulator = simulator;

        StreamInterface stream = this.simulator.getModel().getStream("default");
        this.leadTime = new DistUniform(stream, 0.5, 1.0);
    }

    /** {@inheritDoc} */
    @Override
    public void order(final BuyerInterface buyer, final long amount)
    {
        try
        {
            this.simulator.scheduleEvent(new SimEvent<Double>(this.simulator.getSimulatorTime() + this.leadTime.draw(), buyer,
                    "receiveProduct", new Long[] {Long.valueOf(amount)}));
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().error(exception, "order");
        }
    }
}
