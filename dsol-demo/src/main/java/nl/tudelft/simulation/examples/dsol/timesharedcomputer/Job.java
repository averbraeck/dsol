package nl.tudelft.simulation.examples.dsol.timesharedcomputer;

import nl.tudelft.simulation.dsol.formalisms.flow.StationInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * The Computer job as published in Simulation Modeling and Analysis by A.M. Law &amp; W.D. Kelton section 1.4 and 2.4. <br>
 * Copyright (c) 2003-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="https://simulation.tudelft.nl"> www.simulation.tudelft.nl </a> <br>
 * License of use: <a href="http://www.gnu.org/copyleft/gpl.html">General Public License (GPL) </a>, no warranty <br>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/peterja/index.htm">Peter Jacobs </a>
 */
public class Job
{
    /** arrivalTime refers to the time when the job arrived at the cpu. */
    private double creationTime = Double.NaN;

    /** serviceTime refers to the handling or service time of the job. */
    private double serviceTime = Double.NaN;

    /** source refers to the source of the job. */
    private StationInterface source;

    /**
     * constructs a new Job.
     * @param serviceTimeDistribution DistContinuous; the distribution from which to draw the serviceTime
     * @param source StationInterface; the source of the job
     * @param creationTime double; time of creation
     */
    public Job(final DistContinuous serviceTimeDistribution, final StationInterface source, final double creationTime)
    {
        this.source = source;
        this.serviceTime = serviceTimeDistribution.draw();
        this.creationTime = creationTime;
    }

    /**
     * gets the creationTime of the Job.
     * @return double the time of creation
     */
    public double getCreationTime()
    {
        return this.creationTime;
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
     * @param serviceTime double; the time
     */
    public void setServiceTime(final double serviceTime)
    {
        this.serviceTime = serviceTime;
    }

    /**
     * returns the source.
     * @return StationInterface the owning terminal
     */
    public StationInterface getOwner()
    {
        return this.source;
    }
}
