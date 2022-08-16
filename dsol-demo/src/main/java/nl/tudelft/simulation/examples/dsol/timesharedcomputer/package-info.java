/**
 * Simulation of a time shared computer model.
 * <p>
 * The next example illustrates a time-shared computer facility considered by Adiri and Avi-Itzhak(1969). A company has a
 * computer system consisting of a single central processing unit (CPU) and n terminals. The operator of each terminal "thinks"
 * for an amount of time that is an exponential random variable with a mean of 25seconds and then sends to the CPU a job having
 * service time distributed exponentially with mean=0.8seconds. Arriving jobs join a single queue for the CPU but are served in
 * a round-robin rather than FIFO manner. That is, the CPU allocates to each jobs a maximum service quantum of length q=0.1
 * second. If the remaining service time of a job (s) is no more than q, the CPU spends s seconds plus a fixed swap time x=0.015
 * second, processing the job, which then returns to its terminal. However, if s&gt;q. the CPU spends q+x second processing the
 * job, which then joins the end of the queue, and its remaining service time is decremented by q seconds. This process is
 * repeated until the job's service is eventually completed at which point it returns to its terminal, whose operator begins
 * another think time.
 * </p>
 * <p>
 * We simulate this system for 1000 job completions. and estimate the expected average response times of these jobs, the
 * expected time-average number of jobs waiting in queue and the expected utilization of the CPU. All terminals are in the think
 * state at t=0. The company would like to know how many terminals it can have on its system and still provide users with an
 * average response time of no more than 30 seconds.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
package nl.tudelft.simulation.examples.dsol.timesharedcomputer;
