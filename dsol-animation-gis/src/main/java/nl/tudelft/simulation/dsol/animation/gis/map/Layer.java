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
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Layer implements LayerInterface
{
    /** */
    private static final long serialVersionUID = 20201015L;

    /** the name of the layer. */
    private String name;

    /** whether to display the layer. */
    private boolean display = true;

    /** whether to transform the layer. */
    private boolean transform = false;

    /** the feature map, implemented by a LinkedHashMap to guarantee a reproducible order. */
    private List<FeatureInterface> features = new ArrayList<>();

    /** {@inheritDoc} */
    @Override
    public List<FeatureInterface> getFeatures()
    {
        return this.features;
    }

    /** {@inheritDoc} */
    @Override
    public void setFeatures(final List<FeatureInterface> features)
    {
        this.features = features;
    }

    /** {@inheritDoc} */
    @Override
    public void addFeature(final FeatureInterface feature)
    {
        this.features.add(feature);
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(final String name)
    {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDisplay()
    {
        return this.display;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplay(final boolean status)
    {
        this.display = status;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTransform()
    {
        return this.transform;
    }

    /** {@inheritDoc} */
    @Override
    public void setTransform(final boolean transform)
    {
        this.transform = transform;
    }

}
