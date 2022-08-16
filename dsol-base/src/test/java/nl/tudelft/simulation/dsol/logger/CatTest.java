package nl.tudelft.simulation.dsol.logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * CatTest.java.
 * <p>
 * Copyright (c) 2019-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class CatTest
{
    /**
     * Test the Cat class.
     */
    @Test
    public void testCat()
    {
        assertEquals("LogCategory.DSOL", Cat.DSOL.toString());
        assertEquals("LogCategory.EVENT", Cat.EVENT.toString());
        assertEquals("LogCategory.HLA", Cat.HLA.toString());
        assertEquals("LogCategory.NAMING", Cat.NAMING.toString());
        assertEquals("LogCategory.SWING", Cat.SWING.toString());
        assertEquals("LogCategory.WEB", Cat.WEB.toString());
        assertNotEquals(Cat.DSOL, Cat.EVENT);
        assertNotEquals(Cat.DSOL.hashCode(), Cat.EVENT.hashCode());
    }
    
}
