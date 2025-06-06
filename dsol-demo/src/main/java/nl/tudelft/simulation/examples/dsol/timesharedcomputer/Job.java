package nl.tudelft.simulation.examples.dsol.timesharedcomputer;

import nl.tudelft.simulation.dsol.formalisms.flow.Entity;
import nl.tudelft.simulation.dsol.formalisms.flow.FlowBlock;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * The Computer job as published in Simulation Modeling and Analysis by A.M. Law &amp; W.D. Kelton section 1.4 and 2.4. <br>
 * Copyright (c) 2003-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="https://simulation.tudelft.nl"> www.simulation.tudelft.nl </a> <br>
 * License of use: <a href="http://www.gnu.org/copyleft/gpl.html">General Public License (GPL) </a>, no warranty <br>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/peterja/index.htm">Peter Jacobs </a>
 */
public class Job extends Entity<Double>
{
    /** arrivalTime refers to the time when the job arrived at the cpu. */
    private double creationTime = Double.NaN;

    /** serviceTime refers to the handling or service time of the job. */
    private double serviceTime = Double.NaN;

    /** source refers to the source of the job. */
    private FlowBlock<Double, ?> source;

    /**
     * constructs a new Job.
     * @param id the job id
     * @param serviceTimeDistribution the distribution from which to draw the serviceTime
     * @param source the source of the job
     * @param simulator the simulator to retrieve the creation time of the job
     */
    public Job(final String id, final DistContinuous serviceTimeDistribution, final FlowBlock<Double, ?> source,
            final DevsSimulatorInterface<Double> simulator)
    {
        super(id, simulator);
        this.source = source;
        this.serviceTime = serviceTimeDistribution.draw();
        this.creationTime = simulator.getSimulatorTime();
    }

    /**
     * returns the serviceTime.
     * @return double the time
     */
    public double getServiceTime()
    {
        return this.serviceTime;
    }

    /**
     * sets the serviceTime.
     * @param serviceTime the time
     */
    public void setServiceTime(final double serviceTime)
    {
        this.serviceTime = serviceTime;
    }

    /**
     * returns the source.
     * @return Station the owning terminal
     */
    public FlowBlock<Double, ?> getOwner()
    {
        return this.source;
    }
}
