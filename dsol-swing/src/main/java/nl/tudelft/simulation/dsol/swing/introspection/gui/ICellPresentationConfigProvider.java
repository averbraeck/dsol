package nl.tudelft.simulation.dsol.swing.introspection.gui;

import nl.tudelft.simulation.dsol.swing.introspection.mapping.CellPresentationConfiguration;

/**
 * Allows discovery of a cell presentation configuration {see
 * nl.tudelft.simulation.introspection.mapping.CellPresentationConfiguration}.
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
public interface ICellPresentationConfigProvider
{
    /**
     * @return the cellPresentationConfiguration
     */
    CellPresentationConfiguration getCellPresentationConfiguration();
}
