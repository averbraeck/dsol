package nl.tudelft.simulation.dsol.swing.introspection.sortable;

/**
 * The SortDefinition.
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
public class SortDefinition implements Sortable.Definition
{
    /** the fieldId. */
    private final int fieldID;

    /** whether sorting should occur ascending. */
    private boolean ascending;

    /**
     * constructs a new SortDefinition.
     * @param fieldID the fieldID
     * @param ascending whether sorting should occur ascending
     */
    public SortDefinition(final int fieldID, final boolean ascending)
    {
        this.fieldID = fieldID;
        this.ascending = ascending;
    }

    @Override
    public int getFieldID()
    {
        return this.fieldID;
    }

    @Override
    public boolean isAcendingSort()
    {
        return this.ascending;
    }

    @Override
    public void setAscending(final boolean ascending)
    {
        this.ascending = ascending;
    }

    @Override
    public String toString()
    {
        return "Sort definition, fieldID: " + this.fieldID + ", ascending: " + this.ascending;
    }
}
