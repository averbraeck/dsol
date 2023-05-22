# Random Numbers

## Random Streams

A random stream is a reproducible sequence of random numbers. The stream is initialized with a so-called **seed**. The seed uniquely determines the first number of the random stream. Each following number is determined by the previous number(s) generated. In that sense, simulation random streams are _pseudo-random_, and not really random. When really random results are needed, the random stream can be initialized with `System.currentTimeMillis()` as the seed. 

DSOL makes it possible to draw numbers from different, independent random streams. This is important for enabling experiments where certain events stay the same across experiments, whereas others differ stochastically. This is also known as the method of **Common Random Numbers**, a variance reduction method. Take for example a complex queuing model, where experiments are carried out with different server settings. The 'best' way to statistically compare these server settings is when the arrival events for replication 1 of experiment 1 are exactly the same as the arrival events for replication 1 of the other experiments. The arrival events between replicaion 1 and replication 2 differ, however. When the arrival events are drawn from a different random stream than all other events in the model, this can be easily accomplished.


## RNG implementations

DSOL offers various implementations of Random Number Generators, each of which implements the `StreamInterface`:

|  Class  |  Period  |  Description  |
|  :-----          |  :-----          |  :-----          |
|  `Java2Random` |  10^14 |  Wrapper around the Java `Random` class. This is a 48-bit linear congruential random number generator (Lehmer, 1951) with _X(n+1) = (a X(n) + c) mod m_. In this equation _a_ and _c_ are constants and _m_ is a 48-bit modulus value (Knuth, 1998). The period of 10^14 is relatively short. |
|  `DX120Generator` | 10^1120 | This generator is described by Deng and Xu (2003) in the paper "A System of High-dimensional, Efficient, Long-cycle and Portable Uniform Random Number Generators" at [http://www.cs.memphis.edu/~dengl/dx-rng/dengxu2002.pdf](http://www.cs.memphis.edu/~dengl/dx-rng/dengxu2002.pdf). |
|  `MersenneTwister` | 10^6000 | This is a Java version of the C-program for MT19937: Integer version. genrand() generates one pseudorandom unsigned integer (32 bit) which is uniformly distributed among 0 to 2^32-1 for each call. sgenrand(seed) set initial values to the working area of 624 words. (seed is any 32-bit integer except for 0). Orignally Coded by Takuji Nishimura, considering the suggestions by Topher Cooper and Marc Rieffel in July-August 1997. More information can be found at [https://en.wikipedia.org/wiki/Mersenne_Twister](https://en.wikipedia.org/wiki/Mersenne_Twister) and [http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/ARTICLES/mt.pdf](http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/ARTICLES/mt.pdf). |


## Access to RNGs from the DsolModel

The streams to be used are defined in the model itself. The `DsolModel` interface has several methods defined that can help with retrieving and making of new RNGs. Because the user has to choose the Random Number Generator (RNG) type(s) and seed(s), only one default RNG is defined. The `AbstractDsolModel` class creates a default MersenneTwister RNG with the name "default". Modelers who don't want to bother with the setting of a specific RNG can use this RNG, which has a different seed for each replication.

In the model, the RNG can be used as follows; in this case for creating an Exponential interarrival distribution with a lambda-value of 10.0 in the model's `constructModel()` method:

```java
StreamInterface defaultStream = this.simulator.getReplication().getStream("default");
DistContinuous intervalDist = new DistExponential(defaultStream, 10.0);
```


## The StreamInterface

In DSOL, each Random Number Generator is specified by the `StreamInterface`:

```java
public interface StreamInterface extends Serializable
{
    /**
     * Returns the next pseudorandom <code>boolean</code> value from this random number 
     * generator's sequence. The values <code>true</code> and <code>false</code> are produced with
     * (approximately) equal probability.
     */
    boolean nextBoolean();

    /**
     * Method return a (pseudo)random number from the stream over the interval (0,1) using this stream, after advancing
     * its state by one step.
     */
    double nextDouble();

    /**
     * Method return a (pseudo)random number from the stream over the interval (0,1) using this stream, after advancing
     * its state by one step.
     */
    float nextFloat();

    /**
     * Method return a (pseudo)random number from the stream over using this stream, after advancing its state by one
     * step.
     */
    int nextInt();

    /**
     * Method returns (pseudo)random number from the stream, uniformely 
     * distributed between the integers i and j .
     */
    int nextInt(int i, int j);

    /**
     * Method return a (pseudo)random number from the stream over using this stream.
     */
    long nextLong();

    /**
     * returns the seed of the generator.
     */
    long getSeed();

    /**
     * sets the seed of the generator.
     */
    void setSeed(long seed);

    /**
     * resets the stream.
     */
    void reset();

    /**
     * save the state of the RNG into an object, e.g. to roll it back to this state.
     */
    Object saveState() throws StreamException;

    /**
     * restore the state to an earlier saved state object.
     */
    void restoreState(Object state) throws StreamException;
}
```

