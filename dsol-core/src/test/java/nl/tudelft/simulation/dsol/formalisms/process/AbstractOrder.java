/*
 * AbstractBehaviour.java - created Feb 18, 2005 Copyright (C) 2005 TBA
 * Nederland Vulcanusweg 259, 2624 AV Delft, the Netherlands All rights
 * reserved.
 */

package nl.tudelft.simulation.dsol.formalisms.process;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulator;
import nl.tudelft.simulation.dsol.simulators.Simulator;

/**
 * An abstract order.
 * @author peter
 */
public class AbstractOrder extends Process
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** our BWM. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    String car1 = "bmw";

    /** our second car. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    String car2 = null;

    /**
     * The abstract order.
     * @param car the car
     * @param scheduledTime the scheduled time
     * @param simulator the simulator to schedule on
     */
    public AbstractOrder(final String car, final double scheduledTime, final Simulator simulator)
    {
        super((DEVSSimulator) simulator);
        this.car2 = car;
    }

    /**
     * the process method.
     */
    @Override
    public void process()
    {
        System.out.println("Print the cars");
        System.out.println("car1 =" + this.car1);
        System.out.println("car2 =" + this.car2);
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "AbstractOrder";
    }
}
