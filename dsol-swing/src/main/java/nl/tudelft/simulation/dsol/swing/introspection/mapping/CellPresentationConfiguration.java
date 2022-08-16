package nl.tudelft.simulation.dsol.swing.introspection.mapping;

/**
 * An interface defining the services of a table presentation configuration. It is used to initialize tables with renderers and
 * editors for different cell types. Renderer and editor classes are not checked for type-safety. Normally, they should be
 * assignable from {@link javax.swing.table.TableCellRenderer}.
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
public interface CellPresentationConfiguration
{
    /**
     * Returns all the cell-renderer combinations available in this configuration
     * @return A double class array of cardinality M-2. Tuple [i][j] defines M cell class - render class combinations, with 'i'
     *         identifying the row. 'j=0' identifies the cell class, 'j=1' identifies the renderer class.
     */
    Class<?>[][] getRenderers();

    /**
     * Returns all the cell-editor combinations available in this configuration
     * @return A double class array of cardinality M-2. Tuple [i][j] defines M cell class - editor class combinations, with 'i'
     *         identifying the row. 'j=0' identifies the cell class, 'j=1' identifies the editor class.
     */
    Class<?>[][] getEditors();
}
