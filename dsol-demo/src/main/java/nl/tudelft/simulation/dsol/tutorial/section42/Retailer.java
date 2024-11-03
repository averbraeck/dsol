package nl.tudelft.simulation.dsol.tutorial.section42;

import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.tutorial.section42.policies.OrderingPolicy;
import nl.tudelft.simulation.dsol.tutorial.section42.policies.StationaryPolicy;

/**
 * A Retailer.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Retailer extends LocalEventProducer implements BuyerInterface, SellerInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** TOTAL_ORDERING_COST_EVENT is fired whenever ordering occurs. */
    public static final EventType TOTAL_ORDERING_COST_EVENT = new EventType("TOTAL_ORDERING_COST_EVENT");

    /** INVENTORY_LEVEL_EVENT is fired on changes in inventory. */
    public static final EventType INVENTORY_LEVEL_EVENT = new EventType("INVENTORY_LEVEL_EVENT");

    /** BACKLOG_LEVEL is fired on BACKLOG_LEVEL changes. */
    public static final EventType BACKLOG_LEVEL = new EventType("BACKLOG_LEVEL");

    /** the actual inventoryLevel. */
    private long inventory = 60L;

    /** the ordering backlog. */
    private long backLog = 0L;

    /** the simulator on which to schedule. */
    private DevsSimulatorInterface<Double> simulator = null;

    /** the warehouse we use. */
    private SellerInterface warehouse = null;

    /** the orderingPolicy. */
    private OrderingPolicy orderingPolicy = null;

    /** the costs. */
    private double backlogCosts;

    /** the costs. */
    private double holdingCosts;

    /** the costs. */
    private double marginalCosts;

    /** the costs. */
    private double setupCosts;

    /**
     * constructs a new Retailer.
     * @param simulator DevsSimulatorInterface&lt;Double&gt;; the simulator on which we can schedule
     * @param warehouse SellerInterface; the warehouse to buy at
     */
    public Retailer(final DevsSimulatorInterface<Double> simulator, final SellerInterface warehouse)
    {
        super();
        this.simulator = simulator;
        this.warehouse = warehouse;
        this.orderingPolicy = new StationaryPolicy(simulator);
        try
        {
            InputParameterMap parameters = simulator.getModel().getInputParameterMap();
            this.backlogCosts = (Double) parameters.get("retailer.backlogCosts").getCalculatedValue();
            this.holdingCosts = (Double) parameters.get("retailer.holdingCosts").getCalculatedValue();
            this.marginalCosts = (Double) parameters.get("retailer.marginalCosts").getCalculatedValue();
            this.setupCosts = (Double) parameters.get("retailer.setupCosts").getCalculatedValue();
        }
        catch (InputParameterException ipe)
        {
            ipe.printStackTrace();
            System.exit(-1);
        }
        this.reviewInventory();
    }

    @Override
    public void receiveProduct(final long amount)
    {
        long served = this.backLog - Math.max(0, this.backLog - amount);
        this.backLog = Math.max(0, this.backLog - amount);
        this.inventory = this.inventory + Math.max(0, amount - served);
        this.fireTimedEvent(INVENTORY_LEVEL_EVENT, this.inventory, this.simulator.getSimulatorTime());
        this.fireTimedEvent(BACKLOG_LEVEL, this.backLog, this.simulator.getSimulatorTime());
    }

    /**
     * reviews the inventoryLevel and possibly orders.
     */
    private void reviewInventory()
    {
        double costs = this.holdingCosts * this.inventory + this.backlogCosts * this.backLog;
        long amount = this.orderingPolicy.computeAmountToOrder(this.inventory);
        if (amount > 0)
        {
            costs = costs + this.setupCosts + amount * this.marginalCosts;
            this.fireTimedEvent(TOTAL_ORDERING_COST_EVENT, costs, this.simulator.getSimulatorTime());
            this.warehouse.order(this, amount);
        }
        try
        {
            this.simulator.scheduleEvent(
                    new SimEvent<Double>(this.simulator.getSimulatorTime() + 1.0, this,"reviewInventory", null));
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().error(exception, "reviewInventory");
        }
    }

    @Override
    public void order(final BuyerInterface buyer, final long amount)
    {
        long actualOrderSize = Math.min(amount, this.inventory);
        this.inventory = this.inventory - actualOrderSize;
        if (actualOrderSize < amount)
        {
            this.backLog = this.backLog + (amount - actualOrderSize);
        }
        this.fireTimedEvent(INVENTORY_LEVEL_EVENT, this.inventory, this.simulator.getSimulatorTime());
        this.fireTimedEvent(BACKLOG_LEVEL, this.backLog, this.simulator.getSimulatorTime());
        buyer.receiveProduct(actualOrderSize);
    }

}
