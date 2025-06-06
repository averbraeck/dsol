package nl.tudelft.simulation.dsol.swing.introspection.table;

import javax.swing.table.TableModel;

/**
 * An interface that defines methods for adding and deleting rows from a tablemodel.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>.
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>.
 * @author Niels Lang.
 * @since 1.5
 */
public interface DynamicTableModel extends TableModel
{
    /**
     * Deletes a specific row from the TableModel.
     * @param index The (TableModel) index of the row to be deleted
     */
    void deleteRow(int index);

    /**
     * Deletes a specific set of rows from the TableModel.
     * @param indices The (TableModel) indices of the rows to be deleted
     */
    void deleteRows(int[] indices);

    /**
     * Creates a new row at the end of the TableModel.
     */
    void createRow();

    /**
     * Creates a number of new rows at the end of the TableModel.
     * @param amount The number of rows to be created.
     */
    void createRows(int amount);

    /**
     * @return whether or not the rows in this model can be edited. If false, calls to create and delete methods will have no
     *         final result.
     */
    boolean isRowEditable();
}
