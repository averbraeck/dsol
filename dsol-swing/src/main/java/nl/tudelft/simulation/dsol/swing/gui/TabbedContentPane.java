package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.Component;

import javax.swing.JTabbedPane;

/**
 * A tabbed pane for the DsolPanel to structure console tabs, statistics tabs, animation tabs, etc.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/mzhang">Mingxin Zhang </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck </a>
 */
public class TabbedContentPane extends JTabbedPane
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for TabbedContentPane.
     */
    public TabbedContentPane()
    {
        super();
    }

    /**
     * Constructor for TabbedContentPane.
     * @param tabPlacement the position in the tab
     */
    public TabbedContentPane(final int tabPlacement)
    {
        super(tabPlacement);
    }

    /**
     * Constructor for TabbedContentPane.
     * @param tabPlacement the position in the tab
     * @param tabLayoutPolicy the policy for laying out tabs when all tabs will not fit on one run
     */
    public TabbedContentPane(final int tabPlacement, final int tabLayoutPolicy)
    {
        super(tabPlacement, tabLayoutPolicy);
    }

    /**
     * add a tab with a title at a specific position in the array of tabs.
     * @param position the position in the tabs
     * @param title the title of the tab
     * @param component the swing component to link to the tab
     * @throws IndexOutOfBoundsException when the tab number smaller than 0 or larger than size
     */
    public void addTab(final int position, final String title, final Component component) throws IndexOutOfBoundsException
    {
        component.setName(title);
        this.add(component, position);
    }
}
