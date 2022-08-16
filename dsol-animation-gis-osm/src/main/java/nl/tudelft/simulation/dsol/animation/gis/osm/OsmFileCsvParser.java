package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.djutils.exceptions.Throw;

import de.siegmar.fastcsv.reader.NamedCsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRow;
import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.LayerInterface;
import nl.tudelft.simulation.dsol.animation.gis.map.Feature;
import nl.tudelft.simulation.dsol.animation.gis.map.GisMap;
import nl.tudelft.simulation.dsol.animation.gis.map.Layer;
import nl.tudelft.simulation.dsol.animation.gis.parser.ColorParser;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;

/**
 * This class parses a CSV file that defines which elements of an OpenSTreetMap file need to be drawn and what format to use.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * <p>
 * The dsol-animation-gis project is based on the gisbeans project that has been part of DSOL since 2002, originally by Peter
 * Jacobs and Paul Jacobs.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class OsmFileCsvParser
{
    /** Utility class, no constructor. */
    private OsmFileCsvParser()
    {
        // Utility class
    }

    /**
     * Parses a CSV file with information about the map and layers.
     * @param csvUrl URL; the url of the CSV file.
     * @param osmUrl URL; the OpenStreetMap file in pbf, osm.gz or osm.bz2 format
     * @param mapName String; the human readable name of the map
     * @return MapInterface the parsed map file.
     * @throws IOException on failure
     */
    public static GisMapInterface parseMapFile(final URL csvUrl, final URL osmUrl, final String mapName) throws IOException
    {
        return parseMapFile(csvUrl, osmUrl, mapName, new CoordinateTransform.NoTransform());
    }

    /**
     * Parses a CSV file with information about the map and layers.
     * @param csvUrl URL; the url of the CSV file.
     * @param osmUrl URL; the OpenStreetMap file in pbf, osm.gz or osm.bz2 format
     * @param mapName String; the human readable name of the map
     * @param coordinateTransform CoordinateTransform; the transformation of (x, y) coordinates to (x', y') coordinates.
     * @return MapInterface the parsed map file.
     * @throws IOException on failure
     */
    public static GisMapInterface parseMapFile(final URL csvUrl, final URL osmUrl, final String mapName,
            final CoordinateTransform coordinateTransform) throws IOException
    {
        return parseMapFile(csvUrl, osmUrl, mapName, coordinateTransform, ',', '"');
    }

    /**
     * Parses a CSV file with information about the map and layers. FieldSeparateor can be, for instance \t for a tab character,
     * and the quote character can be the double quote, the single quote, or something else.
     * @param csvUrl URL; the url of the CSV file.
     * @param osmUrl URL; the OpenStreetMap file in pbf, osm.gz or osm.bz2 format
     * @param mapName String; the human readable name of the map
     * @param coordinateTransform CoordinateTransform; the transformation of (x, y) coordinates to (x', y') coordinates.
     * @param fieldSeparator char; field separator, e.g., comma for csv files and tab for tsv files
     * @param quoteCharacter char; e.g., single or double quote
     * @return MapInterface the parsed map file.
     * @throws IOException on failure reading the CSV file, the shape files, or making the layers
     * @throws IllegalArgumentException when one of the outline or fill colors can not be parsed
     * @throws NumberFormatException when one of colors contains an illegal number
     */
    public static GisMapInterface parseMapFile(final URL csvUrl, final URL osmUrl, final String mapName,
            final CoordinateTransform coordinateTransform, final char fieldSeparator, final char quoteCharacter)
            throws IOException
    {
        GisMapInterface map = new GisMap();
        map.setName(mapName);
        List<String> layerNames = new ArrayList<>(); // to get index # in case layers are not sorted in the file
        List<LayerInterface> layerList = new ArrayList<>(); // to pass to Map
        List<FeatureInterface> featuresToRead = new ArrayList<>();

        Reader reader = new InputStreamReader(csvUrl.openStream());
        Throw.when(reader == null, IOException.class, "Cannot find CSV file with OSM shape file information at " + csvUrl);
        NamedCsvReader csvReader =
                NamedCsvReader.builder().fieldSeparator(fieldSeparator).quoteCharacter(quoteCharacter).build(reader);
        Set<String> header = csvReader.getHeader();
        if (!header.contains("layerName") || !header.contains("key") || !header.contains("value")
                || !header.contains("outlineColor") || !header.contains("fillColor") || !header.contains("display")
                || !header.contains("transform"))
        {
            throw new IOException("OSM GIS map csv-file header row did not contain all column headers\n" + header.toString());
        }

        Iterator<NamedCsvRow> it = csvReader.iterator();
        while (it.hasNext())
        {
            NamedCsvRow row = it.next();

            String layerName = row.getField("layerName");
            String key = row.getField("key");
            String value = row.getField("value");
            Color outlineColor = ColorParser.parse(row.getField("outlineColor"));
            Color fillColor = ColorParser.parse(row.getField("fillColor"));
            boolean display = row.getField("display").toLowerCase().startsWith("t");
            boolean transform = row.getField("transform").toLowerCase().startsWith("t");

            LayerInterface layer = null;
            if (layerNames.contains(layerName))
            {
                layer = layerList.get(layerNames.indexOf(layerName));
            }
            else
            {
                layer = new Layer();
                layerList.add(layer);
                layer.setName(layerName);
                layerNames.add(layerName);
            }

            Feature feature = new Feature();
            feature.setKey(key);
            feature.setValue(value);
            feature.setOutlineColor(outlineColor);
            feature.setFillColor(fillColor);
            layer.addFeature(feature);
            featuresToRead.add(feature);
            layer.setDisplay(display);
            layer.setTransform(transform);
        }
        reader.close();
        csvReader.close();

        map.setLayers(layerList);
        OsmFileReader osmReader = new OsmFileReader(osmUrl, coordinateTransform, featuresToRead);
        osmReader.populateShapes();
        return map;
    }

}
