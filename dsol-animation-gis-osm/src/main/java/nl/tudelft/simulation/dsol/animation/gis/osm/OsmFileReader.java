package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.gis.DataSourceInterface;
import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;

/**
 * OsmFileReader reads one layer from an OpenStreetMap file based on the given specifications. The supported formats are pbf,
 * osm, osm.gz, and osm.bz2.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class OsmFileReader implements DataSourceInterface
{
    /** the URL for the osm file to be read. */
    private URL osmURL = null;

    /** an optional transformation of the lat/lon (or other) coordinates. */
    private final CoordinateTransform coordinateTransform;

    /** the features to read by this OpenStreeetMap reader. */
    private final List<FeatureInterface> featuresToRead;

    /**
     * Constructs a new reader for a layer in an OSM shape file.
     * @param osmURL URL can have several valid extensions: .pbf, .osm, .osm.gz, and .osm.bz2
     * @param coordinateTransform the transformation of (x, y) coordinates to (x', y') coordinates.
     * @param featuresToRead the features to read
     * @throws IOException throws an IOException if the url is not accessible
     */
    public OsmFileReader(final URL osmURL, final CoordinateTransform coordinateTransform,
            final List<FeatureInterface> featuresToRead) throws IOException
    {
        this.osmURL = osmURL;
        this.coordinateTransform = coordinateTransform;
        this.featuresToRead = featuresToRead;
    }

    @Override
    public List<FeatureInterface> getFeatures()
    {
        return this.featuresToRead;
    }

    @Override
    public void populateShapes() throws IOException
    {
        String filename = this.osmURL.toString().toLowerCase();
        File inputFile = new File(this.osmURL.getPath());

        OsmEntityProcessor processor = new OsmEntityProcessor(this.featuresToRead, this.coordinateTransform);
        OsmFormat osmFormat = null;

        if (filename.endsWith(".pbf"))
        {
            osmFormat = OsmFormat.PBF;
        }
        else if (filename.endsWith(".gz"))
        {
            osmFormat = OsmFormat.GZIP;
        }
        else if (filename.endsWith(".bz2"))
        {
            osmFormat = OsmFormat.BZIP2;
        }
        else if (filename.endsWith(".osm"))
        {
            osmFormat = OsmFormat.OSM;
        }
        else
        {
            throw new IOException("Unknown OSM format based on file extension: " + filename);
        }

        if (osmFormat.equals(OsmFormat.PBF))
        {
            try
            {
                CategoryLogger.always().info("osm pbf map to read: " + filename);
                processor.initialize();
                new OsmPbfReader(inputFile, processor);
                processor.complete();
            }
            catch (Exception exception)
            {
                throw new IOException("Error during reading of OSM file " + filename, exception);
            }
        }
        else
        {
            try
            {
                CategoryLogger.always().info("osm xml map to read: " + filename);
                processor.initialize();
                new OsmXmlReader(inputFile, processor, osmFormat);
                processor.complete();
            }
            catch (Exception exception)
            {
                throw new IOException("Error during reading of OSM file " + filename, exception);
            }
        }
        CategoryLogger.always().info("OSM layer has been read");
    }

    @Override
    public URL getURL()
    {
        return this.osmURL;
    }

    @Override
    public boolean isDynamic()
    {
        return false; // OSM data is static
    }

}
