package nl.tudelft.simulation.dsol.swing.introspection.mapping;

import java.awt.Color;
import java.awt.Component;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import org.djutils.immutablecollections.ImmutableCollection;
import org.djutils.immutablecollections.ImmutableMap;

import nl.tudelft.simulation.dsol.swing.introspection.gui.ExpandButton;

/**
 * A default implementation of the {see CellPresentationConfiguration} interface. Editors and renders are provided for the
 * JComponent, Color and Font classes. Furthermore, a special editor is provided for the ExpandButton class, to implement the
 * pop-up behaviour of the {see nl.tudelft.simulation.introspection.gui.ExpandButton}.
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
public class DefaultConfiguration implements CellPresentationConfiguration
{
    /** the defaultConfiguation. */
    private static DefaultConfiguration defaultConfig;
    static
    {
        defaultConfig = new DefaultConfiguration();
        defaultConfig.addRenderer(ExpandButton.class, SwingCellRenderer.class);
        defaultConfig.addRenderer(JComponent.class, MyDefaultRenderer.class);
        defaultConfig.addRenderer(Object.class, MyDefaultRenderer.class);
        defaultConfig.addRenderer(Object[].class, ArrayRenderer.class);
        defaultConfig.addRenderer(Collection.class, CollectionRenderer.class);
        defaultConfig.addRenderer(ImmutableCollection.class, ImmutableCollectionRenderer.class);
        defaultConfig.addRenderer(Map.class, MapRenderer.class);
        defaultConfig.addRenderer(ImmutableMap.class, ImmutableMapRenderer.class);
        defaultConfig.addRenderer(Color.class, MyColorRenderer.class);
        defaultConfig.addEditor(Color.class, MyColorEditor.class);
        defaultConfig.addEditor(ExpandButton.class, SwingCellEditor.class);
        defaultConfig.addEditor(JComponent.class, MyDefaultEditor.class);
        defaultConfig.addEditor(Object.class, MyDefaultEditor.class);
        defaultConfig.addEditor(ExpandButton.class, ExpandButtonEditor.class);
    }

    /** the renderers. */
    private Set<Class<?>[]> renderers = new LinkedHashSet<Class<?>[]>();

    /** the editors. */
    private Set<Class<?>[]> editors = new LinkedHashSet<Class<?>[]>();

    /**
     * @return Returns the defaultConfiguration
     */
    public static CellPresentationConfiguration getDefaultConfiguration()
    {
        return defaultConfig;
    }

    /**
     * adds a renderer to the configuration
     * @param cellType the cellType
     * @param renderingClass the renderingClass
     */
    protected synchronized void addRenderer(final Class<?> cellType, final Class<?> renderingClass)
    {
        this.renderers.add(new Class[] {cellType, renderingClass});
    }

    /**
     * adds an editingClass to a cellType
     * @param cellType the cellType
     * @param editingClass an editingClass
     */
    protected void addEditor(final Class<?> cellType, final Class<?> editingClass)
    {
        this.editors.add(new Class[] {cellType, editingClass});
    }

    @Override
    public Class<?>[][] getRenderers()
    {
        return this.renderers.toArray(new Class[0][0]);
    }

    @Override
    public Class<?>[][] getEditors()
    {
        return this.editors.toArray(new Class[0][0]);
    }
}
