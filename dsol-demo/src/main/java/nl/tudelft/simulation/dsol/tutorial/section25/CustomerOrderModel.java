package nl.tudelft.simulation.dsol.tutorial.section25;

import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;

/**
 * The Customer Ordering model class as presented in section 2.5 in the DSOL tutorial..
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class CustomerOrderModel extends AbstractDsolModel<Double, DevsSimulator<Double>>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new customer ordering model.
     * @param simulator DevsSimulator&lt;Double&gt;; the simulator
     */
    public CustomerOrderModel(final DevsSimulator<Double> simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel()
    {
        System.out.println("\nReplication starts...");
        new Customer(this.simulator);
    }
}
