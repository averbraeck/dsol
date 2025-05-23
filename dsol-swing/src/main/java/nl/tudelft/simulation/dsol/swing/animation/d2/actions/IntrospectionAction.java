package nl.tudelft.simulation.dsol.swing.animation.d2.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import nl.tudelft.simulation.dsol.swing.introspection.gui.IntrospectionDialog;
import nl.tudelft.simulation.introspection.DelegateIntrospection;

/**
 * Introspecting an object on the screen.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://www.peter-jacobs.com/index.htm">Peter Jacobs </a>
 * @since 1.5
 */
public class IntrospectionAction extends AbstractAction
{
    /** */
    private static final long serialVersionUID = 20140909L;

    /** the target to introspect. */
    private Object target = null;

    /**
     * constructs a new IntrospectionAction.
     * @param target the target to introspect
     */
    public IntrospectionAction(final Object target)
    {
        super(DelegateIntrospection.checkDelegation(target));
        this.target = target;
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
        new IntrospectionDialog(this.target);
    }
}
