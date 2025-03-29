package nl.tudelft.simulation.dsol.tutorial.section42.policies;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * A StationaryPolicy.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class StationaryPolicy implements OrderingPolicy
{
    /** the lower bound of the policy. */
    private long lowerBound;

    /** the upper bound of the policy. */
    private long upperBound;

    /**
     * constructs a new StationaryPolicy.
     * @param simulator the simulator which is executing the experiment
     */
    public StationaryPolicy(final SimulatorInterface<Double> simulator)
    {
        super();
        try
        {
            InputParameterMap parameters = simulator.getModel().getInputParameterMap();
            this.lowerBound = (Long) parameters.get("policy.lowerBound").getCalculatedValue();
            this.upperBound = (Long) parameters.get("policy.upperBound").getCalculatedValue();
        }
        catch (InputParameterException ipe)
        {
            ipe.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public long computeAmountToOrder(final long inventory)
    {
        if (inventory <= this.lowerBound)
        { return this.upperBound - inventory; }
        return 0;
    }
}
