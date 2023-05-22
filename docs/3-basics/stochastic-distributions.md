# Stochastic Distributions

## Distribution functions

DSOL has a number of built-in continuous and discrete distribution functions. Continuous distribution functions, such as the Exponential, Uniform and Normal distribution, return real numbers. Discrete distributions, such as the Geometric and the Poisson distribution, return integer numbers. Each distribution uses a random stream (see [Random Numbers](../random-numbers)) to provide a pseudo-random draw from the distribution function. 

In most cases, distribution functions for the model are created in the `constructModel()` method of the `DsolModel`. An example is:

```java
StreamInterface defaultStream = this.simulator.getReplication().getStream("default");
DistContinuous processingDist = new DistTriangular(defaultStream, 1.0, 5.0, 12.0);
```

Each time when `processingDist.draw()` is called, a new number drawn from the Triangular distribution is returned. 

The following continuous distributions are provided in DSOL:

* Beta 
* Constant 
* Empirical
* Erlang 
* Exponential 
* Gamma 
* Normal 
* LogNormal 
* Pearson 5 
* Pearson 6
* Triangular
* Uniform
* Weibull

The following discrete distributions are provided in DSOL:

* Bernoulli
* Binomial
* Empirical
* Constant
* DiscreteUniform
* Geometric
* NegativeBinomial
* Poisson


## Distributions typed with units

For the units defined in the [djunits](https://djunits.org) project such as the Length, Time, Duration and Speed, wrappers are available that enable values to be drawn from scalars of a particular unit. The unit-based wrapper wraps a continuous distribution function. This works as follows:

```java
StreamInterface defaultStream = this.simulator.getReplication().getStream("default");
DistContinuous dc = new DistTriangular(defaultStream, 1.0, 5.0, 12.0);
DistContinuousDuration processingDist = new DistContinuousDuration(dc, DurationUnit.MINUTE);
```

Now a draw from `processingDist` will return a `Duration` with a value between 1 and 12 minutes:

```java
Duration duration = processingDist.draw();
```


## Distributions typed with absolute SimTime

For the eight defined `SimTime` types such as the `TimeDouble`, `TimeDoubleUnit`, and `CaendarDouble`, wrappers are available that enable values to be drawn from time instants of that `SimTime` type. The wrapper wraps a continuous distribution function. This works as follows:

```java
StreamInterface defaultStream = this.simulator.getReplication().getStream("default");
DistContinuous dc = new DistUniform(defaultStream, 100.0, 200.0);
DistContinuousSimTime.TimeDoubleUnit eventTimeDist = 
    new DistContinuousSimTime.TimeDoubleUnit(dc, TimeUnit.BASE_HOUR);
```

Now a draw from `eventTimeDist` will return a `SimTimeDoubleUnit` with a value uniformely distributed between 100 and 200 hours after the start of the simulation run:

```java
SimTimeDoubleUnit eventTime = eventTimeDist.draw();
```


## Distributions typed with relative SimTime

For the eight defined `SimTime` types such as the `TimeDouble`, `TimeDoubleUnit`, and `CalendarDouble`, wrappers are available that enable values to be drawn from the relative time belonging to the `SimTime` type. The wrapper wraps a continuous distribution function. This works as follows:

```java
StreamInterface defaultStream = this.simulator.getReplication().getStream("default");
DistContinuous dc = new DistLogNormal(defaultStream, 25.0, 2.0);
DistContinuousSimulationTime.CalendarDouble procDist = 
    new DistContinuousSimulationTime.CalendarDouble(dc, DurationUnit.SECOND);
```

Now a draw from `procDist` will return a `Duration` with a value drawn from the LogNormal distribution _mu_ = 25.0 and _sigma_ = 2.0:

```java
Duration duration = procDist.draw();
```

