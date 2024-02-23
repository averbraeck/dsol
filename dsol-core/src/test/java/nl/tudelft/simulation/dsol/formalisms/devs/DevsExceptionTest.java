package nl.tudelft.simulation.dsol.formalisms.devs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.formalisms.devs.esdevs.exceptions.PortAlreadyDefinedException;
import nl.tudelft.simulation.dsol.formalisms.devs.esdevs.exceptions.PortNotFoundException;
import nl.tudelft.simulation.dsol.formalisms.devs.esdevs.exceptions.StateVariableNotFoundException;

/**
 * PortAlreadyDefinedExceptionTest.java.
 * <p>
 * Copyright (c) 2019- 2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DevsExceptionTest
{
    /**
     * Test PortAlreadyDefinedException.
     */
    @Test
    public void testPortAlreadyDefinedException()
    {
        Exception e1 = new PortAlreadyDefinedException();
        assertEquals(null, e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testPortAlreadyDefinedException", e1.getStackTrace()[0].getMethodName());

        e1 = new PortAlreadyDefinedException("error");
        assertEquals("error", e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testPortAlreadyDefinedException", e1.getStackTrace()[0].getMethodName());

        e1 = new PortAlreadyDefinedException(new RuntimeException("rte"));
        assertTrue(e1.getMessage().contains("RuntimeException"));
        assertTrue(e1.getMessage().contains("rte"));
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testPortAlreadyDefinedException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new PortAlreadyDefinedException("error", new RuntimeException("rte"));
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testPortAlreadyDefinedException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());
    }

    /**
     * Test PortNotFoundException.
     */
    @Test
    public void testPortNotFoundException()
    {
        Exception e1 = new PortNotFoundException();
        assertEquals(null, e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testPortNotFoundException", e1.getStackTrace()[0].getMethodName());

        e1 = new PortNotFoundException("error");
        assertEquals("error", e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testPortNotFoundException", e1.getStackTrace()[0].getMethodName());

        e1 = new PortNotFoundException(new RuntimeException("rte"));
        assertTrue(e1.getMessage().contains("RuntimeException"));
        assertTrue(e1.getMessage().contains("rte"));
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testPortNotFoundException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new PortNotFoundException("error", new RuntimeException("rte"));
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testPortNotFoundException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());
    }

    /**
     * Test StateVariableNotFoundException.
     */
    @Test
    public void testStateVariableNotFoundException()
    {
        Exception e1 = new StateVariableNotFoundException();
        assertEquals(null, e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testStateVariableNotFoundException", e1.getStackTrace()[0].getMethodName());

        e1 = new StateVariableNotFoundException("error");
        assertEquals("error", e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testStateVariableNotFoundException", e1.getStackTrace()[0].getMethodName());

        e1 = new StateVariableNotFoundException(new RuntimeException("rte"));
        assertTrue(e1.getMessage().contains("RuntimeException"));
        assertTrue(e1.getMessage().contains("rte"));
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testStateVariableNotFoundException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new StateVariableNotFoundException("error", new RuntimeException("rte"));
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testStateVariableNotFoundException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());
    }

}
