package nl.tudelft.simulation.dsol.animation.gis;

import java.io.Serializable;
import java.util.List;

/**
 * This interface defines the layer of the map.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface LayerInterface extends Serializable
{
    /**
     * Return the feature definitions for elements that belong to this Layer.
     * @return List&lt;Feature&gt;; the feature definitions for elements that belong to this Layer
     */
    List<FeatureInterface> getFeatures();

    /**
     * Set the feature definitions for elements that belong to this Layer. T
     * @param features List&lt;Feature&gt;; the feature definitions for elements that belong to this Layer
     */
    void setFeatures(List<FeatureInterface> features);

    /**
     * Add a feature to the feature list.
     * @param feature Feature; the feature to addd to the list of features to draw
     */
    void addFeature(FeatureInterface feature);

    /**
     * Return the layer name.
     * @return String; layer name
     */
    String getName();

    /**
     * Set the layer name.
     * @param name String; layer name
     */
    void setName(String name);

    /**
     * Return the display status of the layer (displayed or not).
     * @return boolean; the display status of the layer (displayed or not)
     */
    boolean isDisplay();

    /**
     * Set the display status of the layer (displayed or not).
     * @param status boolean; the display status of the layer (displayed or not)
     */
    void setDisplay(boolean status);

    /**
     * Return the status for the transformation: should the transform be used for this layer or not?
     * @return boolean; the status for the transformation: should the transform be used for this layer or not?
     */
    boolean isTransform();

    /**
     * Set the status for the transformation: should the transform be used for this layer or not?
     * @param transform boolean; the status for the transformation: should the transform be used for this layer or not?
     */
    void setTransform(boolean transform);
}
