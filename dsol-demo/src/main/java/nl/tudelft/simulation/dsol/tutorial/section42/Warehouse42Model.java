package nl.tudelft.simulation.dsol.tutorial.section42;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterLong;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.SimTally;

/**
 * A BoatModel.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Warehouse42Model extends AbstractDsolModel<Double, DevsSimulator<Double>>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** ordering costs statistic. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimTally<Double> orderingCosts;

    /** inventory statistic. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimPersistent<Double> inventory;

    /** backlog statistic. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimPersistent<Double> backlog;

    /**
     * constructs a new BoatModel.
     * @param simulator the simulator
     * @throws InputParameterException on parameter error
     */
    public Warehouse42Model(final DevsSimulator<Double> simulator) throws InputParameterException
    {
        super(simulator);
        InputParameterMap retailerMap = new InputParameterMap("retailer", "Retailer", "Retailer", 1.0);
        retailerMap.add(new InputParameterDouble("backlogCosts", "Backlog costs", "Backlog costs", 1.0, 1.0));
        retailerMap.add(new InputParameterDouble("holdingCosts", "Holding costs", "Holding costs", 1.0, 2.0));
        retailerMap.add(new InputParameterDouble("marginalCosts", "Marginal costs", "Marginal costs", 3.0, 3.0));
        retailerMap.add(new InputParameterDouble("setupCosts", "Setup costs", "Setup costs", 30.0, 4.0));
        this.inputParameterMap.add(retailerMap);
        InputParameterMap policyMap = new InputParameterMap("policy", "Policy", "Policy", 2.0);
        policyMap.add(new InputParameterLong("lowerBound", "Lower bound", "Lower bound", 8L, 1.0));
        policyMap.add(new InputParameterLong("upperBound", "Upper bound", "Upper bound", 80L, 2.0));
        this.inputParameterMap.add(policyMap);
    }

    @Override
    public void constructModel() throws SimRuntimeException
    {
        SellerInterface warehouse = new Warehouse(this.simulator);
        Retailer retailer = new Retailer(this.simulator, warehouse);
        new Customer(this.simulator, retailer);

        try
        {
            this.orderingCosts =
                    new SimTally<Double>("oc", "orderingCosts", this, retailer, Retailer.TOTAL_ORDERING_COST_EVENT);
            this.inventory = new SimPersistent<Double>("il", "inventory level", this, retailer, Retailer.INVENTORY_LEVEL_EVENT);
            this.backlog = new SimPersistent<Double>("bl", "backlog level", this, retailer, Retailer.BACKLOG_LEVEL);
        }
        catch (Exception exception)
        {
            throw new SimRuntimeException(exception);
        }
    }
}
