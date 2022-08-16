/*
 * Created on May 8, 2004
 */
package nl.tudelft.simulation.dsol.swing.introspection.gui;

/**
 * Manages the object model classes for an object model. Allows object models to instantiate appropriate new object models.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>.
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>.
 * @author Niels Lang.
 */
public interface ModelManager
{
    /**
     * @return Returns the class of the default collection object table model
     */
    Class<?> getDefaultCollectionObjectTableModel();

    /**
     * @return Returns the class of the default object table model
     */
    Class<?> getDefaultObjectTableModel();

    /**
     * @return Returns the class of the default map table model
     */
    Class<?> getDefaultMapObjectTableModel();

    /**
     * sets the class of the default collection object table model.
     * @param defaultCollectionObjectTableModel Class&lt;?&gt;; the class
     */
    void setDefaultCollectionObjectTableModel(Class<?> defaultCollectionObjectTableModel);

    /**
     * sets the class of the default object table model.
     * @param defaultObjectTableModel Class&lt;?&gt;; the class
     */
    void setDefaultObjectTableModel(Class<?> defaultObjectTableModel);

    /**
     * sets the class of the default map table model.
     * @param defaultMapTableModel Class&lt;?&gt;; the class
     */
    void setDefaultMapObjectTableModel(Class<?> defaultMapTableModel);
}
