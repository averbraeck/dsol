package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisObject;
import nl.tudelft.simulation.dsol.animation.gis.SerializablePath;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;

/**
 * OsmLayerSink.java.
 * <p>
 * Copyright (c) 2021-2021 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OsmLayerSink implements Sink
{
    /** the ways in the OSM file. */
    private Map<Long, Way> ways = new HashMap<Long, Way>();

    /** the nodes in the OSM file. */
    private Map<Long, Node> nodes = new HashMap<Long, Node>();

    /** the key - value pairs to read. There can be multiple values per key, or '*' for all. */
    private final List<FeatureInterface> featuresToRead;

    /** an optional transformation of the lat/lon (or other) coordinates. */
    private final CoordinateTransform coordinateTransform;

    /**
     * Construct a sink to read the features form the OSM file. For OSM this the Feature list is typically the complete set of
     * features that needs to be read. It is rare that multiple OSM files are available for the data, but this could be the case
     * (e.g., state or country OSM files).<br>
     * TODO: add an optional initial extent in case the sounrce's extent is much larger than the extent we want to display
     * @param featuresToRead the features that the sink needs to read.
     * @param coordinateTransform CoordinateTransform; the transformation of (x, y) coordinates to (x', y') coordinates.
     */
    public OsmLayerSink(final List<FeatureInterface> featuresToRead, final CoordinateTransform coordinateTransform)
    {
        this.featuresToRead = featuresToRead;
        this.coordinateTransform = coordinateTransform;
    }

    /** {@inheritDoc} */
    @Override
    public void process(final EntityContainer entityContainer)
    {
        Entity entity = entityContainer.getEntity();

        if (entity instanceof Node)
        {
            this.nodes.put(entity.getId(), (Node) entity);
            Iterator<Tag> tagIterator = entity.getTags().iterator();
            while (tagIterator.hasNext())
            {
                Tag tag = tagIterator.next();
                String key = tag.getKey();
                String value = tag.getValue();
                // TODO: look whether we want to display special nodes.
            }
        }

        else if (entity instanceof Way)
        {
            boolean read = false;
            Iterator<Tag> tagIterator = entity.getTags().iterator();
            while (tagIterator.hasNext())
            {
                Tag tag = tagIterator.next();
                String key = tag.getKey();
                String value = tag.getValue();
                for (FeatureInterface feature : this.featuresToRead)
                {
                    if (feature.getKey().equals("*"))
                    {
                        read = true;
                        break;
                    }
                    if (feature.getKey().equals(key))
                    {
                        if (feature.getValue().equals("*") || feature.getValue().equals(value))
                        {
                            read = true;
                            break;
                        }
                    }
                }
                if (read)
                {
                    break;
                }
            }
            if (read)
            {
                this.ways.put(entity.getId(), (Way) entity);
            }
        }

        else if (entity instanceof Relation)
        {
            Iterator<Tag> tagint = entity.getTags().iterator();
            while (tagint.hasNext())
            {
                Tag route = tagint.next();
            }
        }

    }

    /** {@inheritDoc} */
    @Override
    public void initialize(final Map<String, Object> metaData)
    {
        // nothing to do right now.
    }

    /** {@inheritDoc} */
    @Override
    public void complete()
    {
        for (Way way : this.ways.values())
        {
            boolean ready = false;
            Iterator<Tag> tagIterator = way.getTags().iterator();
            while (tagIterator.hasNext())
            {
                Tag tag = tagIterator.next();
                String key = tag.getKey();
                String value = tag.getValue();
                for (FeatureInterface feature : this.featuresToRead)
                {
                    if (feature.getKey().equals("*"))
                    {
                        addWay(way, feature);
                        ready = true;
                        break;
                    }
                    if (feature.getKey().equals(key))
                    {
                        if (feature.getValue().equals("*") || feature.getValue().equals(value))
                        {
                            addWay(way, feature);
                            ready = true;
                            break;
                        }
                    }
                }
                if (ready)
                {
                    break;
                }
            }
        }
    }

    /**
     * Add a way to a feature.
     * @param way Way; the way to add to the feature shape list
     * @param feature FeatureInterface; the feature to which this way belongs
     */
    private void addWay(final Way way, final FeatureInterface feature)
    {
        List<WayNode> wayNodes = way.getWayNodes();
        SerializablePath path = new SerializablePath(Path2D.WIND_NON_ZERO, wayNodes.size());
        boolean start = false;
        for (WayNode wayNode : wayNodes)
        {
            float[] coordinate;
            if (wayNode.getNodeId() != 0)
            {
                Node node = this.nodes.get(wayNode.getNodeId());
                coordinate = this.coordinateTransform.floatTransform(node.getLongitude(), node.getLatitude());
            }
            else
            {
                coordinate = this.coordinateTransform.floatTransform(wayNode.getLongitude(), wayNode.getLatitude());
            }
            if (!start)
            {
                path.moveTo(coordinate[0], coordinate[1]);
                start = true;
            }
            path.lineTo(coordinate[0], coordinate[1]);
        }
        String[] att = new String[0];
        feature.getShapes().add(new GisObject(path, att));
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        // nothing to do right now.
    }

}
