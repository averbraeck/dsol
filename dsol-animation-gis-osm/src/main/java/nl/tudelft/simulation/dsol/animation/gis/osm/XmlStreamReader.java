package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;

import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionActivator;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.common.SaxParserFactory;
import org.openstreetmap.osmosis.xml.v0_6.impl.OsmHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XmlStreamReader is a variant on XmlReader from the Osmosis library that takes a stream rather than a file as input to enable
 * parsing of osm files that happen to be inside a jar file, or online. This file is based on the class XmlRreader from the
 * osmosis-xml project.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class XmlStreamReader implements RunnableSource
{
    /** The logger. */
    private static Logger log = Logger.getLogger(XmlStreamReader.class.getName());

    /** the sink to use for actual processing of the data. */
    private Sink sink;

    /** the InputStream to read the data from (rather than the file from XmlReader). */
    private InputStream inputStream;

    /** true is parse dates; false is use current date (faster). */
    private final boolean enableDateParsing;

    /** the compression method to use. */
    private final CompressionMethod compressionMethod;

    /**
     * Create a new XmlReader based on an InputStream rather than on a File.
     * @param inputStream InputStream; the stream to read from
     * @param enableDateParsing boolean; If true, dates will be parsed from xml data, else the current date will be used thus
     *            saving parsing time.
     * @param compressionMethod CompressionMethod; Specifies the compression method to employ
     */
    public XmlStreamReader(final InputStream inputStream, final boolean enableDateParsing,
            final CompressionMethod compressionMethod)
    {
        this.inputStream = inputStream;
        this.enableDateParsing = enableDateParsing;
        this.compressionMethod = compressionMethod;
    }

    /** {@inheritDoc} */
    @Override
    public void setSink(final Sink sink)
    {
        this.sink = sink;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        try
        {
            SAXParser parser;
            this.sink.initialize(Collections.<String, Object>emptyMap());
            this.inputStream = new CompressionActivator(this.compressionMethod).createCompressionInputStream(this.inputStream);
            parser = SaxParserFactory.createParser();
            parser.parse(this.inputStream, new OsmHandler(this.sink, this.enableDateParsing));
            this.sink.complete();
        }
        catch (SAXParseException e)
        {
            throw new OsmosisRuntimeException("Unable to parse xml file from input stream.  publicId=(" + e.getPublicId()
                    + "), systemId=(" + e.getSystemId() + "), lineNumber=" + e.getLineNumber() + ", columnNumber="
                    + e.getColumnNumber() + ".", e);
        }
        catch (SAXException e)
        {
            throw new OsmosisRuntimeException("Unable to parse XML.", e);
        }
        catch (IOException e)
        {
            throw new OsmosisRuntimeException("Unable to read XML file from input stream.", e);
        }
        finally
        {
            this.sink.close();

            if (this.inputStream != null)
            {
                try
                {
                    this.inputStream.close();
                }
                catch (IOException e)
                {
                    log.log(Level.SEVERE, "Unable to close input stream.", e);
                }
                this.inputStream = null;
            }
        }
    }

}
