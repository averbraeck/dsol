package nl.tudelft.simulation.dsol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * SimRuntimeExceptionTest.java.
 * <p>
 * Copyright (c) 2019-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class SimRuntimeExceptionTest
{
    /**
     * Test SimRuntimeException.
     */
    @Test
    public void testSimRuntimeException()
    {
        Exception e1 = new SimRuntimeException();
        assertEquals(null, e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testSimRuntimeException", e1.getStackTrace()[0].getMethodName());

        e1 = new SimRuntimeException("error");
        assertEquals("error", e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testSimRuntimeException", e1.getStackTrace()[0].getMethodName());

        e1 = new SimRuntimeException(new RuntimeException("rte"));
        assertTrue(e1.getMessage().contains("RuntimeException"));
        assertTrue(e1.getMessage().contains("rte"));
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testSimRuntimeException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new SimRuntimeException("error", new RuntimeException("rte"));
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testSimRuntimeException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());
    }
}
