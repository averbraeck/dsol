package nl.tudelft.simulation.dsol.tutorial.section45;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.formalisms.process.Process;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulator;

/**
 * A Boat as presented in Birtwistle, 1979, page 12.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Boat extends Process<Double>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** a reference to protect boats from being garbage collection. */
    protected Boat mySelf = null;

    /**
     * the port to enter.
     */
    private Port port = null;

    /** boat number. */
    private static int number = 0;

    /** the description of the boat. */
    private String description = "Boat(";

    /**
     * constructs a new Boat.
     * @param simulator DEVSSimulator&lt;Double,Double,Double&gt;; the simulator to schedule on
     * @param port Port; the port to sail to
     */
    public Boat(final DEVSSimulator<Double> simulator, final Port port)
    {
        super(simulator);
        this.mySelf = this;
        this.port = port;
        this.description = this.description + (Boat.number++) + ") ";
    }

    /** {@inheritDoc} */
    @Override
    public void process()
    {
        try
        {
            double startTime = this.simulator.getSimulatorTime();
            // We seize one jetty
            this.port.getJetties().requestCapacity(1.0, this);
            this.suspendProcess();
            // Now we request 2 tugs
            this.port.getTugs().requestCapacity(2.0, this);
            this.suspendProcess();
            // Now we dock which takes 2 minutes
            this.hold(2.0);
            // We may now release two tugs
            this.port.getTugs().releaseCapacity(2.0);
            // Now we unload
            this.hold(14.0);
            // Now we claim a tug again
            this.port.getTugs().requestCapacity(1.0, this);
            this.suspendProcess();
            System.out.println(this + " am alive @" + super.simulator.getSimulatorTime());
            // We may leave now
            this.hold(2.0);
            System.out.println(this + " am alive @" + super.simulator.getSimulatorTime());
            // We release both the jetty and the tug
            this.port.getTugs().releaseCapacity(1.0);
            System.out.println(this + " am alive @" + super.simulator.getSimulatorTime());
            this.port.getJetties().releaseCapacity(1.0);
            System.out.println(this + " am alive @" + super.simulator.getSimulatorTime());
            System.out.println(
                    this.toString() + "arrived at time=" + startTime + " and left at time=" + this.simulator.getSimulatorTime()
                            + ". ProcessTime = " + (super.simulator.getSimulatorTime() - startTime));
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().error(exception, "process");
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.description;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "Boat";
    }
}
