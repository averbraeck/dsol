package nl.tudelft.simulation.dsol.animation.gis.esri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import javax.naming.NamingException;

import org.djutils.io.URLResource;
import org.junit.Test;

import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisRenderable2D;
import nl.tudelft.simulation.dsol.animation.gis.MapUnits;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.Contextualized;
import nl.tudelft.simulation.naming.context.JVMContext;

/**
 * EsriParserTest tests the parser for the Esri shape files. 
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class EsriParserTest implements Contextualized
{
    /** context for the gis test. */
    private ContextInterface context;
    
    /**
     * Test the XML map parser.
     * @throws IOException on error
     * @throws NamingException on error
     */
    @Test
    public void testXmlParser() throws IOException, NamingException
    {
        this.context = new JVMContext(null, "rootXml");
        URL url = URLResource.getResource("/resources/esri/tudelft.xml");
        assertNotNull(url);
        GisMapInterface map = EsriFileXmlParser.parseMapFile(url);
        assertEquals("tudelft", map.getName());
        assertEquals(MapUnits.DECIMAL_DEGREES, map.getUnits());
        
        GisRenderable2D renderable = new EsriRenderable2D(this, map);
        assertNotNull(renderable);
        assertEquals(map, renderable.getMap());
        assertNotNull(renderable.getBounds());

        this.context.destroySubcontext("animation");
    }

    /**
     * Test the XML map parser.
     * @throws IOException on error
     * @throws NamingException on error
     */
    @Test
    public void testCsvParser() throws IOException, NamingException
    {
        this.context = new JVMContext(null, "rootCsv");
        URL url = URLResource.getResource("/resources/esri/tudelft.csv");
        assertNotNull(url);
        GisMapInterface map = EsriFileCsvParser.parseMapFile(url, "tudelft");
        assertEquals("tudelft", map.getName());
        
        GisRenderable2D renderable = new EsriRenderable2D(this, map);
        assertNotNull(renderable);
        assertEquals(map, renderable.getMap());
        assertNotNull(renderable.getBounds());

        this.context.destroySubcontext("animation");
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface getContext()
    {
        return this.context;
    }
}
