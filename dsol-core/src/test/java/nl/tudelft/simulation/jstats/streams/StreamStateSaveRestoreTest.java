package nl.tudelft.simulation.jstats.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <br>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank"> DSOL License</a>. <br>
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
                assertEquals(r.length(), 30, "r.len != 30 for j=" + j);
                byte[] state = rng.saveState();
                String s = draw(rng, 10);
                assertEquals(s.length(), 30, "s.len != 30 for j=" + j);
                draw(rng, 20);
                rng.restoreState(state);
                String t = draw(rng, 10);
                assertEquals(t.length(), 30, "t.len != 30 for j=" + j);
                assertEquals(s, t, "j=" + j);
            }
            catch (StreamException se)
            {
                fail("StreamException for j=" + j);
            }
        }
    }

}
