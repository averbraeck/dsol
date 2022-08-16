package nl.tudelft.simulation.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * DSOLExceptionTest.java.
 * <p>
 * Copyright (c) 2019-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DSOLExceptionTest
{
    /**
     * Test DSOLException.
     */
    @Test
    public void testDSOLException()
    {
        Exception e1 = new DSOLException();
        assertEquals(null, e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testDSOLException", e1.getStackTrace()[0].getMethodName());

        e1 = new DSOLException("error");
        assertEquals("error", e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testDSOLException", e1.getStackTrace()[0].getMethodName());

        e1 = new DSOLException(new RuntimeException("rte"));
        assertTrue(e1.getMessage().contains("RuntimeException"));
        assertTrue(e1.getMessage().contains("rte"));
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testDSOLException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new DSOLException("error", new RuntimeException("rte"));
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testDSOLException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new DSOLException("error", new RuntimeException("rte"), false, true);
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testDSOLException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());
        assertEquals(0, e1.getSuppressed().length);
    }
    
    /**
     * Test DSOLException.
     */
    @Test
    public void testDSOLRuntimeException()
    {
        Exception e1 = new DSOLRuntimeException();
        assertEquals(null, e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testDSOLRuntimeException", e1.getStackTrace()[0].getMethodName());

        e1 = new DSOLRuntimeException("error");
        assertEquals("error", e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testDSOLRuntimeException", e1.getStackTrace()[0].getMethodName());

        e1 = new DSOLRuntimeException(new RuntimeException("rte"));
        assertTrue(e1.getMessage().contains("RuntimeException"));
        assertTrue(e1.getMessage().contains("rte"));
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testDSOLRuntimeException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new DSOLRuntimeException("error", new RuntimeException("rte"));
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testDSOLRuntimeException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new DSOLRuntimeException("error", new RuntimeException("rte"), false, true);
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testDSOLRuntimeException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());
        assertEquals(0, e1.getSuppressed().length);
    }

}
