package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.Component;

import javax.swing.JTabbedPane;

/**
 * A tabbed pane for the DSOLPanel to structure console tabs, statistics tabs, animation tabs, etc.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/mzhang">Mingxin Zhang </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck </a>
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
     * @param tabPlacement int; the position in the tab
     */
    public TabbedContentPane(final int tabPlacement)
    {
        super(tabPlacement);
    }

    /**
     * Constructor for TabbedContentPane.
     * @param tabPlacement int; the position in the tab
     * @param tabLayoutPolicy int; the policy for laying out tabs when all tabs will not fit on one run
     */
    public TabbedContentPane(final int tabPlacement, final int tabLayoutPolicy)
    {
        super(tabPlacement, tabLayoutPolicy);
    }

    /**
     * add a tab with a title at a specific position in the array of tabs.
     * @param position int; the position in the tabs
     * @param title String; the title of the tab
     * @param component Component; the swing component to link to the tab
     * @throws IndexOutOfBoundsException when the tab number smaller than 0 or larger than size
     */
    public void addTab(final int position, final String title, final Component component) throws IndexOutOfBoundsException
    {
        component.setName(title);
        this.add(component, position);
    }
}
