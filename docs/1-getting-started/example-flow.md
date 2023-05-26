# Example DSOL Queuing model using flow blocks

This is the second example of building a complete model for an M/M/1 queuing system using the flow library of DSOL. The previous topic showed the same model using the event scheduling library.

## Introduction
An M/M/1 queuing system is a system with one server (that's what the 1 stands for in M/M/1), and parts or clients arriving at the server with an exponentially distributed inter-arrival time (the first M denotes that) and an exponentially distributed service time (the second M denotes that). 
For an M/M/1 system to be stable, the average service time, denoted as $\lambda$ should be less than the average inter-arrival time, denoted as $\mu$. The ratio between $\lambda$ and $\mu$ is called $\rho = \lambda / \mu$, and it is also known as the average utilization of the queuing system. 

For a typical queuing system, we are interested in simulating different arrival rates with different service times, and studying the following main output variables:

- queue length (development over time, average, standard deviation, min, max)
- time in queue (development over time, average, standard deviation, min, max)
- time in system (development over time, average, standard deviation, min, max)
- utilization of the server (development over time, average)

If we build a simulation model using the flow library for this, we need the following types of components:

1. **An Entity** that flows through the model
2. **Generator block** that creates entities with an interarrival time drawn from the correct distribution
3. **A Server block with Resource and Queue** that can serve entities for a given time
4. **A Release block** for the entity to leave the model
5. **Output statistics objects** to gather the required statistics
6. **Experiment management** including the replication settings and seed management
7. **Random distributions** to draw the inter-arrival time and service times
8. **Input parameters** for the model to set the experimental conditions
9. **A program** that can be started and that creates the simulator, model, and makes it work.
10. **A GUI** to display statistics and graphs to the user


## Implementation using Flow Blocks
Flow blocks are submodels with input ports and output ports that can be stringed together. Entities flow from the output ports to the input ports, and can influence the behavior of the flow blocks, and be transformed by the flow blocks. Some blocks, like the *Generator* block only have an output port; others like the _Release_ block only have an input port. Typically, flow blocks automatically collect their own statistics, and there are special flow blocks that can collect statistics that the standard flow blocks do not collect.


### 1. An Entity that flows through the system
In the Flow Library, any object can serve as an Entity, 