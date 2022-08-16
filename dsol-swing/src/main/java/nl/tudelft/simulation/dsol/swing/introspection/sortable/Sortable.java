package nl.tudelft.simulation.dsol.swing.introspection.sortable;

/**
 * Defines methods to define, retrieve and perform sorting definitions.
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
 * @since 1.5
 */
public interface Sortable
{
    /**
     * Defines the sort definition.
     */
    public interface Definition
    {
        /**
         * @return Returns the field to which this sorting definition applies
         */
        int getFieldID();

        /**
         * Returns whether this definition defines an ascending sort.
         * @return A 'false' return value implies a descending sort definition.
         */
        boolean isAcendingSort();

        /**
         * Allows dynamic definitions.
         * @param ascending boolean; whether the sort is ascending
         */
        void setAscending(boolean ascending);
    }

    /**
     * @return Returns the current definitions defined for this Sortable. The sequence of the definitions matches the sorting
     *         sequence, in that a definition will be performed before another definition if having a lower index.
     */
    Definition[] getDefinitions();

    /**
     * Sets the current definitions defined for this Sortable. The sequence of the definitions matches the sorting sequence, in
     * that a definition will be performed before another definition if having a lower index.
     * @param definitions Definition[]; An array of sort definitions. If multiple definitions for the same field are included,
     *            the one with highest index will be applied.
     */
    void setDefinitions(Definition[] definitions);

    /**
     * Instructs this Sortable to sort based on currently set sorting definitions.
     */
    void sort();
}
