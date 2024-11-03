package nl.tudelft.simulation.dsol.tutorial.section42.policies;

/**
 * An OrderingPolicy.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public interface OrderingPolicy
{
    /**
     * decides the amount of product to order.
     * @param inventory long; the current inventory
     * @return long the amount
     */
    long computeAmountToOrder(long inventory);
}
