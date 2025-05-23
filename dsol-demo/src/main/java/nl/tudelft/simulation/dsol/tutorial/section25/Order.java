package nl.tudelft.simulation.dsol.tutorial.section25;

/**
 * The Order class as presented in section 2.5 in the DSOL tutorial..
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Order
{
    /** the product of an order. */
    private String product = null;

    /** the amount of product to order. */
    private double amount = Double.NaN;

    /**
     * constructs a new Order.
     * @param product the product
     * @param amount the amount to buy
     */
    public Order(final String product, final double amount)
    {
        super();
        this.product = product;
        this.amount = amount;
    }

    @Override
    public String toString()
    {
        return "Order[" + this.product + ";" + this.amount + "]";
    }
}
