package nl.tudelft.simulation.dsol.animation.gis;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * DataSourceInterface is the connector between the reader of a GIS file and the display in the DSOL animation. Note that the
 * data source can be a 'live' data source, with updates between queries for the shapes! Often, the data source will not do any
 * updates and retrieve the shapes only once. There can be one overall data source (all Features point to the same data source,
 * such as with OSM files), one data source per layer (such as with ESRI shape files), or even individual data sources per
 * feature (e.g., when we have a geo-file per bus line that needs to be colored differently on the map).
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface DataSourceInterface extends Serializable
{
    /**
     * Return the URL of the data source. Note that the data source can be a 'live' data source, with updates between queries
     * for the shapes! Often, the data source will be static and cache the shapes.
     * @return URL; the URL of the data source
     */
    URL getURL();

    /**
     * Return the list of Features for which this data source is responsible. There can be one overall data source (all Features
     * point to the same data source, such as with OSM files), one data source per layer (such as with ESRI shape files), or
     * even individual data sources per feature (e.g., when we have a geo-file per bus line that needs to be colored differently
     * on the map).
     * @return List&lt;Feature&gt;; the Features that the data source should populate when asked
     */
    List<FeatureInterface> getFeatures();

    /**
     * Populate the shape information in the Features. When the data source is not dynamic, this is only done once after
     * construction, so this method does not need to be called again in this case. When it is called again, it will be ignored.
     * When the data source is dynamic, it can re-fill the shapes in the feature cache periodically or when the data has
     * received updates.
     * @throws IOException when there is an issue with reading the data from the data source
     */
    void populateShapes() throws IOException;

    /**
     * Return whether the data source is dynamic or not. If the data source is not dynamic, data is collected only once.
     * @return boolean; whether the data source is dynamic or not
     */
    boolean isDynamic();

}
