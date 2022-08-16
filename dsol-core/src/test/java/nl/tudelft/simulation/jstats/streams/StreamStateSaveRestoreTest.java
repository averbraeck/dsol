package nl.tudelft.simulation.jstats.streams;

import static org.junit.Assert.fail;

import org.junit.Assert;

/**
 * <br>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class StreamStateSaveRestoreTest
{
    /**
     * @param stream the stream to draw from.
     * @param n the number of draws to make.
     * @return a number between 10 and 99
     */
    private String draw(final StreamInterface stream, final int n)
    {
        String s = "";
        for (int i = 0; i < n; i++)
        {
            s += stream.nextInt(10, 100) + " ";
        }
        return s;
    }

    /**
     * tests the classes in the reference class.
     */
    // TODO: @Test testStreamStateSaveRestore
    public void testStreamStateSaveRestore()
    {
        StreamInterface[] streams = {new Java2Random(10), new MersenneTwister(10), new DX120Generator(10)};
        for (int j = 0; j < streams.length; j++)
        {
            try
            {
                StreamInterface rng = streams[j];
                String r = draw(rng, 10);
                Assert.assertEquals("r.len != 30 for j=" + j, r.length(), 30);
                byte[] state = rng.saveState();
                String s = draw(rng, 10);
                Assert.assertEquals("s.len != 30 for j=" + j, s.length(), 30);
                draw(rng, 20);
                rng.restoreState(state);
                String t = draw(rng, 10);
                Assert.assertEquals("t.len != 30 for j=" + j, t.length(), 30);
                Assert.assertEquals("j=" + j, s, t);
            }
            catch (StreamException se)
            {
                fail("StreamException for j=" + j);
            }
        }
    }

}
