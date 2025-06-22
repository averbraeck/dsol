package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

/**
 * OsmXmlReader reads from an OSM XML stream. The stream may be gzip or bzip2 compressed. The reader uses StAX to parse, and
 * works in two passes.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OsmXmlReader
{

    /**
     * Create an XML reader.
     * @param inputFile the input file
     * @param processor the entity processor
     * @param format the format with compression
     * @throws IOException on I/O error
     * @throws XMLStreamException on XML read error
     */
    public OsmXmlReader(final File inputFile, final OsmEntityProcessor processor, final OsmFormat format)
            throws IOException, XMLStreamException
    {
        XMLInputFactory factory = XMLInputFactory.newInstance();

        // First pass: read the nodes
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        InputStreamReader inputStreamReader = null;
        if (format.equals(OsmFormat.GZIP))
        {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fileInputStream));
            inputStreamReader = new InputStreamReader(gzipInputStream);
        }
        else if (format.equals(OsmFormat.BZIP2))
        {
            BZip2CompressorInputStream bzipInputStream =
                    new BZip2CompressorInputStream(new BufferedInputStream(fileInputStream));
            inputStreamReader = new InputStreamReader(bzipInputStream);
        }
        else if (format.equals(OsmFormat.OSM))
        {
            inputStreamReader = new InputStreamReader(new BufferedInputStream(fileInputStream));
        }
        else
        {
            throw new IOException("OsmXmlReader called with wrong format: " + format);
        }
        XMLStreamReader reader = factory.createXMLStreamReader(inputStreamReader);

        while (reader.hasNext())
        {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("node"))
            {
                long id = Long.parseLong(reader.getAttributeValue(null, "id"));
                double lat = Double.parseDouble(reader.getAttributeValue(null, "lat"));
                double lon = Double.parseDouble(reader.getAttributeValue(null, "lon"));
                Node node = new Node(id, lat, lon);
                processor.process(node);
            }
        }
        reader.close();

        // Second pass: read the ways
        fileInputStream = new FileInputStream(inputFile);
        inputStreamReader = null;
        if (format.equals(OsmFormat.GZIP))
        {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fileInputStream));
            inputStreamReader = new InputStreamReader(gzipInputStream);
        }
        else if (format.equals(OsmFormat.BZIP2))
        {
            BZip2CompressorInputStream bzipInputStream =
                    new BZip2CompressorInputStream(new BufferedInputStream(fileInputStream));
            inputStreamReader = new InputStreamReader(bzipInputStream);
        }
        else if (format.equals(OsmFormat.OSM))
        {
            inputStreamReader = new InputStreamReader(new BufferedInputStream(fileInputStream));
        }
        reader = factory.createXMLStreamReader(inputStreamReader);
        boolean inWay = false;
        List<Long> ndRefs = new ArrayList<>();
        long wayId = 0;
        Map<String, String> tags = new HashMap<>();

        while (reader.hasNext())
        {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT)
            {
                String name = reader.getLocalName();
                if (name.equals("way"))
                {
                    inWay = true;
                    ndRefs.clear();
                    tags.clear();
                    wayId = Long.parseLong(reader.getAttributeValue(null, "id"));
                }
                else if (inWay && name.equals("nd"))
                {
                    long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                    ndRefs.add(ref);
                }
                else if (inWay && name.equals("tag"))
                {
                    String k = reader.getAttributeValue(null, "k");
                    String v = reader.getAttributeValue(null, "v");
                    tags.put(k, v);
                }
            }

            if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("way"))
            {
                inWay = false;
                Way w = new Way(wayId, ndRefs, tags);
                processor.process(w);
            }
        }
    }

}
