package nl.tudelft.simulation.naming.context.event;

/**
 * ContextScope defines the area of interest for a Listener for the EventContextInterface. Events can be generated for the
 * current node, for all nodes on the current level (excluding or including the node itself), or for the subtree starting at the
 * current node.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public enum ContextScope
{
    /**
     * Enum constant for expressing interest in events concerning the object named by the target.
     */
    OBJECT_SCOPE,

    /**
     * Enum constant for expressing interest in events concerning objects in the context named by the target, excluding the
     * context named by the target.
     */
    LEVEL_SCOPE,

    /**
     * Enum constant for expressing interest in events concerning objects in the context named by the target, including the
     * context named by the target.
     */
    LEVEL_OBJECT_SCOPE,

    /**
     * Enum constant for expressing interest in events concerning objects in the subtree of the object named by the target,
     * including the object named by the target.
     */
    SUBTREE_SCOPE;
}
