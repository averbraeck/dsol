package nl.tudelft.simulation.dsol.animation.gis.esri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URL;

import javax.naming.NamingException;

import org.djutils.io.ResourceResolver;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisRenderable2d;
import nl.tudelft.simulation.dsol.animation.gis.MapUnits;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.Contextualized;
import nl.tudelft.simulation.naming.context.JvmContext;

/**
 * EsriParserTest tests the parser for the Esri shape files.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
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
        this.context = new JvmContext(null, "rootXml");
        URL url = ResourceResolver.resolve("/resources/esri/tudelft.xml").asUrl();
        assertNotNull(url);
        GisMapInterface map = EsriFileXmlParser.parseMapFile(url);
        assertEquals("tudelft", map.getName());
        assertEquals(MapUnits.DECIMAL_DEGREES, map.getMapUnits());

        GisRenderable2d renderable = new EsriRenderable2d(this, map);
        assertNotNull(renderable);
        assertEquals(map, renderable.getMap());
        assertNotNull(renderable.getRelativeBounds());

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
        this.context = new JvmContext(null, "rootCsv");
        URL url = ResourceResolver.resolve("/resources/esri/tudelft.csv").asUrl();
        assertNotNull(url);
        GisMapInterface map = EsriFileCsvParser.parseMapFile(url, "tudelft");
        assertEquals("tudelft", map.getName());

        GisRenderable2d renderable = new EsriRenderable2d(this, map);
        assertNotNull(renderable);
        assertEquals(map, renderable.getMap());
        assertNotNull(renderable.getRelativeBounds());

        this.context.destroySubcontext("animation");
    }

    @Override
    public ContextInterface getContext()
    {
        return this.context;
    }
}
