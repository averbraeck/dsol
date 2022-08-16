package nl.tudelft.simulation.dsol.interpreter.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * A LineNumber.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class LineNumber
{
    /** the startByte attribute. */
    private int startByte = -1;

    /** the lineNumber attribute. */
    private int lineNumber = -1;

    /**
     * constructs a new LineNumber.
     * @param dataInput DataInput; dataInput to use
     * @throws IOException on failure
     */
    public LineNumber(final DataInput dataInput) throws IOException
    {
        super();
        this.startByte = dataInput.readUnsignedShort();
        this.lineNumber = dataInput.readUnsignedShort();
    }

    /**
     * @return Returns the lineNumber.
     */
    public int getLineNumber()
    {
        return this.lineNumber;
    }

    /**
     * @return Returns the startByte.
     */
    public int getStartByte()
    {
        return this.startByte;
    }
}
