/**
 * Single-server queueing system using event scheduling.
 * <p>
 * Consider a single-server queueing system for which the interarrival times are independent and identically distributed. A
 * customer who arrives and finds the server idle enters service immediately. The service time of the server is again an
 * independent and identically distributed random variable. A customer who enters and finds the server busy joins the end of a
 * single queue. Upon completing service for a customer, the server chooses a customer from the queue in a first-in first-out
 * (FIFO) manner.
 * </p>
 * <p>
 * The MM1 examples illustrated in this package start with a simulator in "empty-and-idle" state. At t=0 we begin with waiting
 * for the first arrival which will occur after the first arrival time. We simulate the system until a fixed number of customers
 * (n) have completed their delays in queue. To measure the performance of this system, we will look at estimates of 3
 * quantities. First we will estimate the expected average delay in queue of the n customers completing their service during the
 * simulation. This time delay is represented as d(n). <br>
 * From a system based viewpoint we are going to look at the average number of entities in the queue q(n) and to the average
 * utilization of the server u(n).
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
package nl.tudelft.simulation.dsol.demo.event.mm1;
