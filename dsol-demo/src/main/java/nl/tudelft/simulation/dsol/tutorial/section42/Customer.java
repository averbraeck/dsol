package nl.tudelft.simulation.dsol.tutorial.section42;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.logger.Cat;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistEmpiricalDiscreteLong;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.empirical.DiscreteEmpiricalDistribution;
import nl.tudelft.simulation.jstats.distributions.empirical.ProbabilityDensities;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * A Customer.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Customer implements BuyerInterface
{
    /** the simulator to schedule on. */
    private DevsSimulatorInterface<Double> simulator = null;

    /** the retailer by whom we order our product. */
    private SellerInterface retailer = null;

    /** the intervalTime between consequtive orders. */
    private DistContinuous intervalTime = null;

    /** the orderBatchSize of an order. */
    private DistDiscrete orderBatchSize = null;

    /**
     * constructs a new Customer.
     * @param simulator DevsSimulatorInterface&lt;Double&gt;; the simulator to schedule on
     * @param retailer SellerInterface; the retailer to buy at. In more advanced examples, we would look up this retailer at a
     *            yellow page.
     */
    public Customer(final DevsSimulatorInterface<Double> simulator, final SellerInterface retailer)
    {
        super();
        this.simulator = simulator;
        this.retailer = retailer;
        StreamInterface stream = this.simulator.getModel().getStream("default");
        this.intervalTime = new DistExponential(stream, 0.1);
        DiscreteEmpiricalDistribution empDist = ProbabilityDensities.createDiscreteDistribution(new long[] {1, 2, 3, 4},
                new double[] {1.0 / 6.0, 1.0 / 3.0, 1.0 / 3.0, 1.0 / 6.0});
        this.orderBatchSize = new DistEmpiricalDiscreteLong(stream, empDist);
        this.createOrder();
    }

    @Override
    public void receiveProduct(final long amount)
    {
        this.simulator.getLogger().filter(Cat.DSOL).trace("receiveProduct: received " + amount);
    }

    /**
     * creates an order.
     */
    private void createOrder()
    {
        this.retailer.order(this, this.orderBatchSize.draw());
        try
        {
            this.simulator.scheduleEvent(new SimEvent<Double>(this.simulator.getSimulatorTime() + this.intervalTime.draw(),
                    this,"createOrder", null));
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().error(exception, "createOrder");
        }
    }
}
