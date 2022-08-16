package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameter;

/**
 * InputField for entering data. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface InputField
{
    /** @return the key of the field. */
    String getKey();

    /** @return the input parameter for the field. */
    InputParameter<?, ?> getParameter();
}
