package nl.tudelft.simulation.dsol.swing.introspection.gui;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>.
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>.
 * @author Niels Lang.
 */
public class DefaultModelManager implements ModelManager
{
    /** the class of the defaultTableModel. */
    protected Class<?> defaultObjectTableModel = ObjectTableModel.class;

    /** the class of the defaultCollectionTableModel. */
    protected Class<?> defaultCollectionObjectTableModel = CollectionTableModel.class;

    /** the class of the defaultMapTableModel. */
    protected Class<?> defaultMapObjectTableModel = MapTableModel.class;

    /**
     * Bean Constructor for the model manager.
     */
    public DefaultModelManager()
    {
        this(ObjectTableModel.class, CollectionTableModel.class, MapTableModel.class);
    }

    /**
     * Constructor for the model manager.
     * @param objectModelClass the objectModelClass
     * @param collectionModelClass the collectionModelClass
     * @param mapModelClass the mapModelClass
     */
    public DefaultModelManager(final Class<?> objectModelClass, final Class<?> collectionModelClass,
            final Class<?> mapModelClass)
    {
        this.defaultObjectTableModel = objectModelClass;
        this.defaultCollectionObjectTableModel = collectionModelClass;
        this.defaultMapObjectTableModel = mapModelClass;
    }

    @Override
    public Class<?> getDefaultCollectionObjectTableModel()
    {
        return this.defaultCollectionObjectTableModel;
    }

    @Override
    public Class<?> getDefaultObjectTableModel()
    {
        return this.defaultObjectTableModel;
    }

    @Override
    public Class<?> getDefaultMapObjectTableModel()
    {
        return this.defaultMapObjectTableModel;
    }

    @Override
    public void setDefaultCollectionObjectTableModel(final Class<?> defaultCollectionObjectTableModel)
    {
        this.defaultCollectionObjectTableModel = defaultCollectionObjectTableModel;
    }

    @Override
    public void setDefaultObjectTableModel(final Class<?> defaultObjectTableModel)
    {
        this.defaultObjectTableModel = defaultObjectTableModel;
    }

    @Override
    public void setDefaultMapObjectTableModel(final Class<?> defaultMapTableModel)
    {
        this.defaultMapObjectTableModel = defaultMapTableModel;
    }
}
