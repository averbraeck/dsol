package nl.tudelft.simulation.jstats.streams;

import java.io.Serializable;

/**
 * The StreamInterface defines the streams to be used within the JSTATS package. Potential implementations include the pseudo
 * random stream, the fully one-time random stream, etc.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public interface StreamInterface extends Serializable
{
    /**
     * Return the next pseudo-random, uniformly distributed boolean value.
     * @return boolean; a pseudo-random boolean with 50/50 chance for true or false
     */
    boolean nextBoolean();

    /**
     * Return a pseudo-random number from the stream over the interval (0,1) using this stream, after advancing its
     * state by one step.
     * @return double; the pseudo-random number
     */
    double nextDouble();

    /**
     * Return a pseudo-random number from the stream over the interval (0,1) using this stream, after advancing its
     * state by one step.
     * @return float; the pseudo-random number
     */
    float nextFloat();

    /**
     * Return a pseudo-random number from the stream over using this stream, after advancing its state by one step.
     * @return int; the pseudo-random number
     */
    int nextInt();

    /**
     * Return pseudo-random number from the stream between the integers i (inclusive) and j (inclusive).
     * @param i int; the minimal value
     * @param j int; the maximum value
     * @return int; a value between i and j
     */
    int nextInt(int i, int j);

    /**
     * Return a pseudo-random number from the stream over using this stream, after advancing its state by one step.
     * @return long; the pseudo-random number
     */
    long nextLong();

    /**
     * Return the seed of the generator.
     * @return long; the seed
     */
    long getSeed();

    /**
     * Return the original seed of the generator with which it has been first initialized.
     * @return long; the original seed of the generator when it was first initialized
     */
    long getOriginalSeed();

    /**
     * Set the seed of the generator.
     * @param seed long; the new seed
     */
    void setSeed(long seed);

    /**
     * Reset the stream to use the original seed with which it was initialized.
     */
    void reset();

    /**
     * Save the state of the RNG into an object, e.g. to roll it back to this state.
     * @return the state as an object specific to the RNG.
     * @throws StreamException when getting the state fails.
     */
    byte[] saveState() throws StreamException;

    /**
     * Restore the state to an earlier saved state object.
     * @param state Object; the earlier saved state to which the RNG rolls back.
     * @throws StreamException when resetting the state fails.
     */
    void restoreState(byte[] state) throws StreamException;
}
