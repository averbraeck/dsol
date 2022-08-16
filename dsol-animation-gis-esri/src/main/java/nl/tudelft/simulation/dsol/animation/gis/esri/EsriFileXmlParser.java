package nl.tudelft.simulation.dsol.animation.gis.esri;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.io.URLResource;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.LayerInterface;
import nl.tudelft.simulation.dsol.animation.gis.MapImageInterface;
import nl.tudelft.simulation.dsol.animation.gis.MapUnits;
import nl.tudelft.simulation.dsol.animation.gis.map.Feature;
import nl.tudelft.simulation.dsol.animation.gis.map.GisMap;
import nl.tudelft.simulation.dsol.animation.gis.map.Layer;
import nl.tudelft.simulation.dsol.animation.gis.map.MapImage;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;

/**
 * This class parses an XML file that defines which elements of shape file(s) need to be drawn and what format to use.
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
public final class EsriFileXmlParser
{
    /** the default mapfile. */
    public static final URL MAPFILE_SCHEMA = URLResource.getResource("/resources/mapfile.xsd");

    /** Utility class, no constructor. */
    private EsriFileXmlParser()
    {
        // Utility class
    }

    /**
     * parses a Mapfile URL to a mapFile.
     * @param url URL; the mapfile url.
     * @return MapInterface the parsed mapfile.
     * @throws IOException on failure
     */
    public static GisMapInterface parseMapFile(final URL url) throws IOException
    {
        return parseMapFile(url, new CoordinateTransform.NoTransform());
    }

    /**
     * parses a Mapfile URL to a mapFile.
     * @param url URL; the mapfile url.
     * @param coordinateTransform CoordinateTransform; the transformation of (x, y) coordinates to (x', y') coordinates.
     * @return MapInterface the parsed mapfile.
     * @throws IOException on failure
     */
    public static GisMapInterface parseMapFile(final URL url, final CoordinateTransform coordinateTransform) throws IOException
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();

            GisMapInterface map = new GisMap();

            // map.name
            map.setName(nodeText(root, "name"));

            // map.units
            if (nodeTagExists(root, "units"))
            {
                map.setUnits(parseUnits(nodeText(root, "units")));
            }

            // map.extent
            map.setExtent(parseExtent(nodeTagItem(root, "extent", 0), coordinateTransform));

            // map.image
            if (nodeTagExists(root, "image"))
            {
                map.setImage(parseImage(nodeTagItem(root, "image", 0)));
            }

            // map.layer
            map.setLayers(parseLayers(root.getElementsByTagName("layer"), coordinateTransform));

            return map;
        }
        catch (DOMException | SAXException | ParserConfigurationException exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * Parses a xml-element representing the units for the map.
     * @param units String; the string representation of the units
     * @return MapUnits enum
     */
    @SuppressWarnings("checkstyle:needbraces")
    private static MapUnits parseUnits(final String units)
    {
        if (units.equals("feet"))
            return MapUnits.FEET;
        if (units.equals("dd"))
            return MapUnits.DECIMAL_DEGREES;
        if (units.equals("inches"))
            return MapUnits.INCHES;
        if (units.equals("kilometers"))
            return MapUnits.KILOMETERS;
        if (units.equals("meters"))
            return MapUnits.METERS;
        if (units.equals("miles"))
            return MapUnits.MILES;
        return MapUnits.METERS;
    }

    /**
     * Creates the extent for the map, in transformed units.
     * @param node Node; the dom node
     * @param coordinateTransform CoordinateTransform; the transformation to apply on the coordinates
     * @return Bounds2d; the extent for the map, in transformed units
     * @throws IOException on parsing error
     */
    private static Bounds2d parseExtent(final Node node, final CoordinateTransform coordinateTransform) throws IOException
    {
        try
        {
            double minX = nodeDouble(node, "minX");
            double minY = nodeDouble(node, "minY");
            double maxX = nodeDouble(node, "maxX");
            double maxY = nodeDouble(node, "maxY");

            double[] p = coordinateTransform.doubleTransform(minX, minY);
            double[] q = coordinateTransform.doubleTransform(maxX, maxY);
            minX = Math.min(p[0], q[0]);
            minY = Math.min(p[1], q[1]);
            maxX = Math.max(p[0], q[0]);
            maxY = Math.max(p[1], q[1]);
            return new Bounds2d(minX, maxX, minY, maxY);
        }
        catch (Exception exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * parses a xml-element representing the Image.
     * @param node Node; the map.image dom node
     * @return information about the image
     * @throws IOException on parsing error
     */
    @SuppressWarnings("checkstyle:needbraces")
    private static MapImageInterface parseImage(final Node node) throws IOException
    {
        Element element = (Element) node;
        MapImageInterface mapImage = new MapImage();
        try
        {
            if (nodeTagExists(node, "backgroundColor"))
                mapImage.setBackgroundColor(parseColor(nodeTagItem(element, "backgroundColor", 0)));
            if (nodeTagExists(node, "size"))
                mapImage.setSize(parseDimension(nodeTagItem(element, "size", 0)));
            return mapImage;
        }
        catch (Exception exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * parses a xml-element representing a Color.
     * @param node Node; the node to parse for the color
     * @return Color of element
     * @throws IOException on parsing error
     */
    private static Color parseColor(final Node node) throws IOException
    {
        try
        {
            int r = nodeInt(node, "r");
            int g = nodeInt(node, "g");
            int b = nodeInt(node, "b");
            if (nodeTagExists(node, "a"))
            {
                int a = nodeInt(node, "a");
                return new Color(r, g, b, a);
            }
            return new Color(r, g, b);
        }
        catch (Exception exception)
        {
            throw new IOException(exception.getMessage());
        }
    }

    /**
     * Parse an xml-element representing a Dimension.
     * @param node Node; the dom node with the dimension information
     * @return Dimension of element
     * @throws IOException on parsing error
     */
    private static Dimension parseDimension(final Node node) throws IOException
    {
        try
        {
            int width = nodeInt(node, "width");
            int height = nodeInt(node, "height");
            return new Dimension(width, height);
        }
        catch (Exception exception)
        {
            throw new IOException(exception.getMessage());
        }
    }

    /**
     * Parse an xml-element representing a Layer.
     * @param layerNodeList NodeList; the list of layer tags in the map
     * @param coordinateTransform CoordinateTransform; the transformation to apply to the layer
     * @return List&lt;LayerInterface&gt;; the list of parsed layers
     * @throws IOException on parsing error
     */
    private static List<LayerInterface> parseLayers(final NodeList layerNodeList, final CoordinateTransform coordinateTransform)
            throws IOException
    {
        try
        {
            List<LayerInterface> layerList = new ArrayList<>();
            for (int i = 0; i < layerNodeList.getLength(); i++)
            {
                Node layerNode = layerNodeList.item(i);
                LayerInterface layer = new Layer();
                layerList.add(layer);
                layer.setName(nodeText(layerNode, "name"));
                Feature feature = new Feature();
                layer.addFeature(feature); // key and value remain at * and *

                Node dataNode = nodeTagItem(layerNode, "data", 0);
                if (nodeTagExists(dataNode, "shapeFile"))
                {
                    String resourceName = nodeText(dataNode, "shapeFile");
                    URL resource = URLResource.getResource(resourceName);
                    if (resource == null)
                    {
                        throw new IOException("Cannot locate shapeFile: " + resourceName);
                    }
                    ShapeFileReader dataSource = new ShapeFileReader(resource, coordinateTransform, layer.getFeatures());
                    dataSource.populateShapes();
                }

                /*-
                if (nodeTagExists(layerNode, "minScale"))
                {
                    layer.setMinScale(nodeInt(layerNode, "minscale"));
                }
                if (nodeTagExists(layerNode, "maxScale"))
                {
                    layer.setMaxScale(nodeInt(layerNode, "maxscale"));
                }
                */
                if (nodeTagExists(layerNode, "fillColor"))
                {
                    feature.setFillColor(parseColor(nodeTagItem(layerNode, "fillColor", 0)));
                }
                if (nodeTagExists(layerNode, "outlineColor"))
                {
                    feature.setOutlineColor(parseColor(nodeTagItem(layerNode, "outlineColor", 0)));
                }
                if (nodeTagExists(layerNode, "display"))
                {
                    layer.setDisplay(nodeBoolean(layerNode, "display"));
                }
                if (nodeTagExists(layerNode, "transform"))
                {
                    layer.setTransform(nodeBoolean(layerNode, "transform"));
                }
            }
            return layerList;
        }
        catch (Exception exception)
        {
            throw new IOException(exception.getMessage());
        }
    }

    /**
     * Check if one or more nodes with the tag name exist, e.g. from: &lt;node&gt;&lt;tag&gt;text&lt;/tag&gt; ... &lt;/node&gt;.
     * @param node Node; the node to check
     * @param tag String; the name of the tag for which we check one copy exists
     * @return boolean; whether the tag count is larger than zero
     * @throws IOException on parse error
     */
    private static boolean nodeTagExists(final Node node, final String tag) throws IOException
    {
        try
        {
            Element element = (Element) node;
            return element.getElementsByTagName(tag).getLength() > 0;
        }
        catch (Exception exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * Return the i-th node with the tag name in the element, e.g. from: &lt;node&gt;&lt;tag&gt;text&lt;/tag&gt; ...
     * &lt;/node&gt;.
     * @param node Node; the node to check
     * @param tag String; the name of the tag for which we check one copy exists
     * @param item int; the number in the list to look up
     * @return Node; the i-th node with the tag name in the element
     * @throws IOException on parse error
     */
    private static Node nodeTagItem(final Node node, final String tag, final int item) throws IOException
    {
        try
        {
            Element element = (Element) node;
            return element.getElementsByTagName(tag).item(item);
        }
        catch (Exception exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * Get the text of a node with the tag name, e.g. from: &lt;node&gt;&lt;tag&gt;text&lt;/tag&gt; ... &lt;/node&gt;.
     * @param node Node; the node to get the text from
     * @param tag String; the name of the tag that contains the text
     * @return String; text enclosed in the tag
     * @throws IOException on parse error
     */
    private static String nodeText(final Node node, final String tag) throws IOException
    {
        try
        {
            Element element = (Element) node;
            return element.getElementsByTagName(tag).item(0).getTextContent();
        }
        catch (Exception exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * Get the double value of a node with the tag name, e.g. from: &lt;node&gt;&lt;tag&gt;123.45&lt;/tag&gt; ... &lt;/node&gt;.
     * @param node Node; the node to get the value from
     * @param tag String; the name of the tag that contains the value
     * @return double; value enclosed in the tag
     * @throws IOException on parse error
     */
    private static double nodeDouble(final Node node, final String tag) throws IOException
    {
        try
        {
            return Double.parseDouble(nodeText(node, tag));
        }
        catch (Exception exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * Get the int value of a node with the tag name, e.g. from: &lt;node&gt;&lt;tag&gt;123&lt;/tag&gt; ... &lt;/node&gt;.
     * @param node Node; the node to get the value from
     * @param tag String; the name of the tag that contains the value
     * @return int; value enclosed in the tag
     * @throws IOException on parse error
     */
    private static int nodeInt(final Node node, final String tag) throws IOException
    {
        try
        {
            return Integer.parseInt(nodeText(node, tag));
        }
        catch (Exception exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * Get the boolean value of a node with the tag name, e.g. from: &lt;node&gt;&lt;tag&gt;true&lt;/tag&gt; ... &lt;/node&gt;.
     * @param node Node; the node to get the value from
     * @param tag String; the name of the tag that contains the value
     * @return boolean; value enclosed in the tag
     * @throws IOException on parse error
     */
    private static boolean nodeBoolean(final Node node, final String tag) throws IOException
    {
        try
        {
            String b = nodeText(node, tag).toLowerCase();
            return b.startsWith("t") || b.startsWith("y") || b.equals("1");
        }
        catch (Exception exception)
        {
            throw new IOException(exception);
        }
    }
}
