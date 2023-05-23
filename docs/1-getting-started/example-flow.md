# Example DSOL Queueing model using flow blocks

This is the second example of building a complete model for an M/M/1 queueing system using the flow library of DSOL. The previous topic showed the same model using the event scheduling library.

## Introduction
An M/M/1 queueing system is a system with one server (that's what the 1 stands for in M/M/1), and parts or clients arriving at the server with an exponentially distributed inter-arrival time (the first M denotes that) and an exponentially distributed service time (the second M denotes that). 
For an M/M/1 system to be stable, the average service time, denoted as $\lambda$ should be less than the average inter-arrival time, denoted as $\mu$. The ratio between $\lambda$ and $\mu$ is called $\rho = \lambda / \mu$, and it is also known as the average utilization of the queueing system. 