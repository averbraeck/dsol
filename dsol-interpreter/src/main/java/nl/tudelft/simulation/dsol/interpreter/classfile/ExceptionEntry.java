package nl.tudelft.simulation.dsol.interpreter.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * A ExceptionEntry.
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
public class ExceptionEntry
{
    /** the start byte of the entry. */
    private final int startByte;

    /** the end byte of the entry. */
    private final int endByte;

    /** the handler number. */
    private final int handler;

    /** the catchType of this handler. */
    private final Class<?> catchType;

    /**
     * constructs a new ExceptionEntry.
     * @param dataInput DataInput; the input to read
     * @param constantPool Constant[]; the constantPool of this entry
     * @throws IOException on IOFailure
     */
    public ExceptionEntry(final DataInput dataInput, final Constant[] constantPool) throws IOException
    {
        super();
        this.startByte = dataInput.readUnsignedShort();
        this.endByte = dataInput.readUnsignedShort();
        this.handler = dataInput.readUnsignedShort();
        int catchTypeIndex = dataInput.readUnsignedShort();

        if (catchTypeIndex > 0)
        {
            try
            {
                this.catchType = ((ConstantClass) constantPool[catchTypeIndex]).getValue().getClassValue();
            }
            catch (Exception exception)
            {
                throw new IOException("could not resolve catchType in ExceptionEntry");
            }
        }
        else
        {
            this.catchType = Exception.class;
        }
    }

    /**
     * @return Returns the catchType.
     */
    public Class<?> getCatchType()
    {
        return this.catchType;
    }

    /**
     * @return Returns the endByte.
     */
    public int getEndByte()
    {
        return this.endByte;
    }

    /**
     * @return Returns the handler.
     */
    public int getHandler()
    {
        return this.handler;
    }

    /**
     * @return Returns the startByte.
     */
    public int getStartByte()
    {
        return this.startByte;
    }

    /**
     * Resolves the exceptionEntry for this particular exceptionType.
     * @param entries ExceptionEntry[]; the entries to choose from
     * @param exceptionType Class&lt;?&gt;; the exception type
     * @param bytePosition int; the position where the exception starts
     * @return the most specific exceptionType. null is no exceptionType is found
     */
    public static ExceptionEntry resolveExceptionEntry(final ExceptionEntry[] entries, final Class<?> exceptionType,
            final int bytePosition)
    {
        if (entries == null || exceptionType == null)
        {
            return null;
        }
        for (int i = 0; i < entries.length; i++)
        {
            if (entries[i].getCatchType().isAssignableFrom(exceptionType) && bytePosition >= entries[i].getStartByte()
                    && bytePosition <= entries[i].getEndByte())
            {
                return entries[i];
            }
        }
        return null;
    }
}
