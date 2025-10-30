package nl.tudelft.simulation.dsol.animation.gis.map;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.LayerInterface;

/**
 * This is an implementation of the LayerInterface that just stores the basic metadata for each layer. The elements to draw are
 * defined by a set of key - value pairs in the Feature. The Map implementation is a LinkedHashMap to enforce a reproducible
 * order. The actual information of the objects in the layer (points, shapes) is contained in the DataSource.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Layer implements LayerInterface
{
    /** the name of the layer. */
    private String name;

    /** zIndex for all the features in the layer. */
    private double zIndex = 0.0;
    
    /** whether to display the layer. */
    private boolean display = true;

    /** whether to transform the layer. */
    private boolean transform = false;

    /** the feature map, implemented by a LinkedHashMap to guarantee a reproducible order. */
    private List<FeatureInterface> features = new ArrayList<>();

    @Override
    public List<FeatureInterface> getFeatures()
    {
        return this.features;
    }

    @Override
    public void setFeatures(final List<FeatureInterface> features)
    {
        this.features = features;
    }

    @Override
    public void addFeature(final FeatureInterface feature)
    {
        this.features.add(feature);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public boolean isDisplay()
    {
        return this.display;
    }

    @Override
    public void setDisplay(final boolean status)
    {
        this.display = status;
    }

    @Override
    public boolean isTransform()
    {
        return this.transform;
    }

    @Override
    public void setTransform(final boolean transform)
    {
        this.transform = transform;
    }

    @Override
    public double getZIndex()
    {
        return this.zIndex;
    }

    @Override
    public void setZIndex(final double zIndex)
    {
        this.zIndex = zIndex;
    }

}
