package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;

import nl.tudelft.simulation.dsol.interpreter.Operation;

/**
 * The factoryInterface defines the required behavior for operation factories mapping opcodes to operations.
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
public interface FactoryInterface
{
    /** RESERVED OPCODe. */
    int BREAKPOINT = 202;

    /** RESERVED OPCODe. */
    int IMPDEP1 = 254;

    /** RESERVED OPCODe. */
    int IMPDEP2 = 255;

    /**
     * resolves an operation for an operandCode.
     * @param dataInput DataInput; the dataInput
     * @param startBytePosition int; the position in the byteStream
     * @return Operation the operation
     * @throws IOException on IOException
     */
    Operation readOperation(final DataInput dataInput, final int startBytePosition) throws IOException;

    /**
     * resolves an operation for an operandCode.
     * @param operand int; the operand
     * @param dataInput DataInput; the dataInput
     * @param startBytePosition int; the position in the byteStream
     * @return Operation the operation
     * @throws IOException on IOException
     */
    Operation readOperation(final int operand, final DataInput dataInput, final int startBytePosition) throws IOException;
}
