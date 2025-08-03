package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.djutils.exceptions.Throw;
import org.json.JSONArray;
import org.json.JSONObject;

import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.LayerInterface;
import nl.tudelft.simulation.dsol.animation.gis.map.Feature;
import nl.tudelft.simulation.dsol.animation.gis.map.GisMap;
import nl.tudelft.simulation.dsol.animation.gis.map.Layer;
import nl.tudelft.simulation.dsol.animation.gis.parser.ColorParser;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;

/**
 * This class parses a JSON file that defines which elements of an OpenSTreetMap file need to be drawn and what format to use.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * <p>
 * The dsol-animation-gis project is based on the gisbeans project that has been part of DSOL since 2002, originally by Peter
 * Jacobs and Paul Jacobs.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public final class OsmFileJsonParser
{
    /** Utility class, no constructor. */
    private OsmFileJsonParser()
    {
        // Utility class
    }

    /**
     * Parses a JSON file with information about the map and layers.
     * @param jsonUrl the url of the JSON file.
     * @param osmUrl the OpenStreetMap file in pbf, osm.gz or osm.bz2 format
     * @param mapName the human readable name of the map
     * @return MapInterface the parsed map file.
     * @throws IOException on failure
     */
    public static GisMapInterface parseMapFile(final URL jsonUrl, final URL osmUrl, final String mapName) throws IOException
    {
        return parseMapFile(jsonUrl, osmUrl, mapName, new CoordinateTransform.NoTransform());
    }

    /**
     * Parses a JSON file with information about the map and layers.
     * @param jsonUrl the url of the JSON file.
     * @param osmUrl the OpenStreetMap file in pbf, osm.gz or osm.bz2 format
     * @param mapName the human readable name of the map
     * @param coordinateTransform the transformation of (x, y) coordinates to (x', y') coordinates.
     * @return MapInterface the parsed map file.
     * @throws IOException on failure
     */
    public static GisMapInterface parseMapFile(final URL jsonUrl, final URL osmUrl, final String mapName,
            final CoordinateTransform coordinateTransform) throws IOException
    {
        return parseMapFile(jsonUrl, osmUrl, mapName, coordinateTransform, ',', '"');
    }

    /**
     * Parses a JSON file with information about the map and layers. FieldSeparateor can be, for instance \t for a tab
     * character, and the quote character can be the double quote, the single quote, or something else.
     * @param jsonUrl the url of the JSON file.
     * @param osmUrl the OpenStreetMap file in pbf, osm.gz or osm.bz2 format
     * @param mapName the human readable name of the map
     * @param coordinateTransform the transformation of (x, y) coordinates to (x', y') coordinates.
     * @param fieldSeparator field separator, e.g., comma for json files and tab for tsv files
     * @param quoteCharacter e.g., single or double quote
     * @return MapInterface the parsed map file.
     * @throws IOException on failure reading the JSON file, the shape files, or making the layers
     * @throws IllegalArgumentException when one of the outline or fill colors can not be parsed
     * @throws NumberFormatException when one of colors contains an illegal number
     */
    public static GisMapInterface parseMapFile(final URL jsonUrl, final URL osmUrl, final String mapName,
            final CoordinateTransform coordinateTransform, final char fieldSeparator, final char quoteCharacter)
            throws IOException
    {
        GisMapInterface map = new GisMap();
        map.setName(mapName);
        List<String> layerNames = new ArrayList<>(); // to get index # in case layers are not sorted in the file
        List<LayerInterface> layerList = new ArrayList<>(); // to pass to Map
        List<FeatureInterface> featuresToRead = new ArrayList<>();

        Reader reader = new InputStreamReader(jsonUrl.openStream());
        Throw.when(reader == null, IOException.class, "Cannot find JSON file with OSM shape file information at " + jsonUrl);
        var br = new BufferedReader(reader);
        String jsonStr = br.lines().collect(Collectors.joining("\n"));
        JSONObject obj = new JSONObject(jsonStr);
        JSONArray jsonLayers = obj.getJSONArray("layers");
        for (int i = 0; i < jsonLayers.length(); i++)
        {
            JSONObject jsonLayer = jsonLayers.getJSONObject(i);
            String layerName = jsonLayer.getString("layer");
            JSONObject defaults = jsonLayer.getJSONObject("default");
            JSONArray items = jsonLayer.getJSONArray("items");
            for (int j = 0; j < items.length(); j++)
            {
                JSONObject item = items.getJSONObject(j);

                String key = item.has("key") ? item.getString("key") : defaults.getString("key");
                String value = item.has("value") ? item.getString("value") : defaults.getString("value");
                Color outlineColor = ColorParser
                        .parse(item.has("outlineColor") ? item.getString("outlineColor") : defaults.getString("outlineColor"));
                Color fillColor = ColorParser
                        .parse(item.has("fillColor") ? item.getString("fillColor") : defaults.getString("fillColor"));
                boolean display = item.has("display") ? item.getBoolean("display") : defaults.getBoolean("display");
                boolean transform = item.has("transform") ? item.getBoolean("transform") : defaults.getBoolean("transform");
                int lineWidthPx = item.has("lineWidth") ? item.getInt("lineWidth")
                        : defaults.has("lineWidth") ? defaults.getInt("lineWidth") : 1;

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
                feature.setLineWidthPx(lineWidthPx);
                layer.addFeature(feature);
                featuresToRead.add(feature);
                layer.setDisplay(display);
                layer.setTransform(transform);
            }
        }
        br.close();
        reader.close();

        map.setLayers(layerList);
        OsmFileReader osmReader = new OsmFileReader(osmUrl, coordinateTransform, featuresToRead);
        osmReader.populateShapes();
        return map;
    }

}
