/**
 * Simulation model of a single server system.
 * <p>
 * This section reveals the DSOL implementation of the single server queuing system. We consider a system consisting of a single
 * server which receives customers arriving independently and identically distributed (IDD). A customer who arrives and finds
 * the server idle is being serviced immediately. A customer who finds the server busy enters a single queue. Upon completing a
 * service for a customer, the server checks the queue and (if any) services the next customer in a first-in, first-out (FIFO)
 * manner.
 * </p>
 * <p>
 * The simulation begins in an empty-and-idle state. We simulate until a predefined fixed number of customers <i>n</i> have
 * entered the system and completed their service. To measure the performance of this system, we focus on a number of output
 * variables. First of all we focus on the expected delay <i>d(n)</i> of a customer in the queue. From a system perspective we
 * furthermore focus on the number of customers in queue <i>q(n)</i>. The final output variable we consider is the expected
 * utilization of the server <i>u(n)</i>. This is the proportion of the time the server was in its busy state. Since the
 * simulation is dependent on random variable observations for both the inter-arrival time and the service time, the output
 * variables <i>d(n), q(n)</i> and <i>u(n)</i> will be random and, therefore, expected to be variable.
 * </p>
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
package nl.tudelft.simulation.dsol.tutorial.section41;
