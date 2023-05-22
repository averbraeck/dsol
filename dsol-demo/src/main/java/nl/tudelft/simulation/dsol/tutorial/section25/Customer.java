package nl.tudelft.simulation.dsol.tutorial.section25;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The Customer class as presented in section 2.5 in the DSOL tutorial..
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Customer
{
    /** the simulator we can schedule on. */
    private DevsSimulatorInterface<Double> simulator = null;

    /**
     * constructs a new Customer.
     * @param simulator DevsSimulatorInterface&lt;Double&gt;; The simulator to use.
     */
    public Customer(final DevsSimulatorInterface<Double> simulator)
    {
        super();
        this.simulator = simulator;
        this.generateOrder();
    }

    /** Generates a new Order. */
    private void generateOrder()
    {
        try
        {
            Order order = new Order("Television", 2.0);
            System.out.println("ordered " + order + " @ time=" + this.simulator.getSimulatorTime());

            // Now we schedule the next action at time = time + 2.0
            SimEvent<Double> simEvent =
                    new SimEvent<Double>(this.simulator.getSimulatorTime() + 2.0, this,"generateOrder", null);
            this.simulator.scheduleEvent(simEvent);
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().error(exception);
        }
    }
}
