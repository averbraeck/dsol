package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.LayerInterface;
import nl.tudelft.simulation.dsol.animation.gis.map.Feature;
import nl.tudelft.simulation.dsol.animation.gis.map.GisMap;
import nl.tudelft.simulation.dsol.animation.gis.map.Layer;
import nl.tudelft.simulation.dsol.animation.gis.parser.ColorParser;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;

/**
 * This class parses a YAML file that defines which elements of an OpenSTreetMap file need to be drawn and what format to use.
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
public final class OsmFileYamlParser
{
    /** Utility class, no constructor. */
    private OsmFileYamlParser()
    {
        // Utility class
    }

    /**
     * Parses a YAML file with information about the map and layers.
     * @param yamlUrl the url of the YAML file.
     * @param osmUrl the OpenStreetMap file in pbf, osm.gz or osm.bz2 format
     * @param mapName the human readable name of the map
     * @return MapInterface the parsed map file.
     * @throws IOException on failure
     */
    public static GisMapInterface parseMapFile(final URL yamlUrl, final URL osmUrl, final String mapName) throws IOException
    {
        return parseMapFile(yamlUrl, osmUrl, mapName, new CoordinateTransform.NoTransform());
    }

    /**
     * Parses a YAML file with information about the map and layers.
     * @param yamlUrl the url of the YAML file.
     * @param osmUrl the OpenStreetMap file in pbf, osm.gz or osm.bz2 format
     * @param mapName the human readable name of the map
     * @param coordinateTransform the transformation of (x, y) coordinates to (x', y') coordinates.
     * @return MapInterface the parsed map file.
     * @throws IOException on failure
     */
    public static GisMapInterface parseMapFile(final URL yamlUrl, final URL osmUrl, final String mapName,
            final CoordinateTransform coordinateTransform) throws IOException
    {
        return parseMapFile(yamlUrl, osmUrl, mapName, coordinateTransform, ',', '"');
    }

    /**
     * Parses a YAML file with information about the map and layers. FieldSeparateor can be, for instance \t for a tab
     * character, and the quote character can be the double quote, the single quote, or something else.
     * @param yamlUrl the url of the YAML file.
     * @param osmUrl the OpenStreetMap file in pbf, osm.gz or osm.bz2 format
     * @param mapName the human readable name of the map
     * @param coordinateTransform the transformation of (x, y) coordinates to (x', y') coordinates.
     * @param fieldSeparator field separator, e.g., comma for yaml files and tab for tsv files
     * @param quoteCharacter e.g., single or double quote
     * @return MapInterface the parsed map file.
     * @throws IOException on failure reading the YAML file, the shape files, or making the layers
     * @throws IllegalArgumentException when one of the outline or fill colors can not be parsed
     * @throws NumberFormatException when one of colors contains an illegal number
     */
    @SuppressWarnings("unchecked")
    public static GisMapInterface parseMapFile(final URL yamlUrl, final URL osmUrl, final String mapName,
            final CoordinateTransform coordinateTransform, final char fieldSeparator, final char quoteCharacter)
            throws IOException
    {
        GisMapInterface map = new GisMap();
        map.setName(mapName);
        List<String> layerNames = new ArrayList<>(); // to get index # in case layers are not sorted in the file
        List<LayerInterface> layerList = new ArrayList<>(); // to pass to Map
        List<FeatureInterface> featuresToRead = new ArrayList<>();

        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(yamlUrl.openStream());
        List<Object> yamlLayers = (List<Object>) obj.get("layers");
        for (int i = 0; i < yamlLayers.size(); i++)
        {
            Map<String, Object> yamlLayer = (Map<String, Object>) yamlLayers.get(i);
            String layerName = (String) yamlLayer.get("layer");
            Map<String, Object> defaults = (Map<String, Object>) yamlLayer.get("default");
            List<Object> items = (List<Object>) yamlLayer.get("items");
            for (int j = 0; j < items.size(); j++)
            {
                Map<String, Object> item = (Map<String, Object>) items.get(j);

                String key = item.containsKey("key") ? (String) item.get("key") : (String) defaults.get("key");
                String value = item.containsKey("value") ? (String) item.get("value") : (String) defaults.get("value");
                Color outlineColor = ColorParser.parse(item.containsKey("outlineColor") ? (String) item.get("outlineColor")
                        : (String) defaults.get("outlineColor"));
                Color fillColor = ColorParser.parse(
                        item.containsKey("fillColor") ? (String) item.get("fillColor") : (String) defaults.get("fillColor"));
                boolean display =
                        item.containsKey("display") ? (Boolean) item.get("display") : (Boolean) defaults.get("display");
                boolean transform =
                        item.containsKey("transform") ? (Boolean) item.get("transform") : (Boolean) defaults.get("transform");
                int lineWidthPx = item.containsKey("lineWidth") ? (Integer) item.get("lineWidth")
                        : defaults.containsKey("lineWidth") ? (Integer) defaults.get("lineWidth") : 1;

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

        map.setLayers(layerList);
        OsmFileReader osmReader = new OsmFileReader(osmUrl, coordinateTransform, featuresToRead);
        osmReader.populateShapes();
        return map;
    }

}
