package nl.tudelft.simulation.dsol.interpreter.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.djutils.reflection.MethodSignature;

import nl.tudelft.simulation.dsol.interpreter.Interpreter;
import nl.tudelft.simulation.dsol.interpreter.InterpreterException;
import nl.tudelft.simulation.dsol.interpreter.Operation;

/**
 * A MethodDescriptor.
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
public class MethodDescriptor
{
    /** bytePositions. */
    private int[] bytePositions = new int[0];

    /** the constantPool to use. */
    private Constant[] constantPool = new Constant[0];

    /** the exceptionTable. */
    private ExceptionEntry[] exceptionTable = new ExceptionEntry[0];

    /** the localVarialbes. */
    private LineNumber[] lineNumberTable = new LineNumber[0];

    /** the localVarialbes. */
    private LocalVariableDescriptor[] localVariableTable = new LocalVariableDescriptor[0];

    /** the maxStack of this method. */
    private int maxStack = -1;

    /** the method. */
    private AccessibleObject method = null;

    /** the methodSignature. */
    private MethodSignature methodSignature = null;

    /** the name of the method. */
    private String name;

    /** the operations. */
    private Operation[] operations = new Operation[0];

    /**
     * constructs a new MethodDescriptor.
     * @param dataInput DataInput; the dataInput
     * @param constantPool Constant[]; the constantPool
     * @throws IOException on ioException
     */
    public MethodDescriptor(final DataInput dataInput, final Constant[] constantPool) throws IOException
    {
        super();
        this.constantPool = constantPool;
        this.readMethod(dataInput);
    }

    /**
     * returns the bytePosition of the operation.
     * @param operationIndex int; the n-th operation
     * @return the bytePostion
     */
    public int getBytePosition(final int operationIndex)
    {
        return this.bytePositions[operationIndex];
    }

    /**
     * returns the exception table of the method.
     * @return the exceptiontable
     */
    public ExceptionEntry[] getExceptionTable()
    {
        return this.exceptionTable;
    }

    /**
     * returns the linenumber table of the method.
     * @return the linenumber table
     */
    public LineNumber[] getLineNumberTable()
    {
        return this.lineNumberTable;
    }

    /**
     * returns the localvariable descriptors.
     * @return the localvairable descriptors
     */
    public LocalVariableDescriptor[] getLocalVariableTable()
    {
        return this.localVariableTable;
    }

    /**
     * returns the maximum stack size of this method.
     * @return int the maximum stacksize of this method
     */
    public int getMaxStack()
    {
        return this.maxStack;
    }

    /**
     * @return Returns the method
     */
    public AccessibleObject getMethod()
    {
        return this.method;
    }

    /**
     * @return Returns the methodSignature
     */
    public MethodSignature getMethodSignature()
    {
        return this.methodSignature;
    }

    /**
     * returns the name of the method.
     * @return the name of the method
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * returns the operations of the method.
     * @return the operations of the method.
     */
    public Operation[] getOperations()
    {
        return this.operations;
    }

    /**
     * returns the index number of the operation in the operationtable of the operation starting at bytePosition.
     * @param bytePosition int; the bytePosition
     * @return the number
     */
    public int getOperationIndex(final int bytePosition)
    {
        for (int i = 0; i < this.bytePositions.length; i++)
        {
            if (this.bytePositions[i] == bytePosition)
            {
                return i;
            }
        }
        throw new NoSuchElementException("At bytePosition(" + bytePosition + ") no operation starts");
    }

    /**
     * returns the operation at bytePosition.
     * @param bytePosition int; the position to start
     * @return the operation
     */
    public Operation getOperation(final int bytePosition)
    {
        for (int i = 0; i < this.bytePositions.length; i++)
        {
            if (this.bytePositions[i] == bytePosition)
            {
                return this.operations[i];
            }
        }
        return null;
    }

    /**
     * sets the method of this descriptor.
     * @param method AccessibleObject; the method
     */
    public void setMethod(final AccessibleObject method)
    {
        this.method = method;
    }

    /** ******************** Private Parsing Methods ************** */

    /**
     * reads the method.
     * @param dataInput DataInput; the dataInput
     * @throws IOException on ioException
     */
    private void readMethod(final DataInput dataInput) throws IOException
    {
        // We skip the access_flag
        dataInput.skipBytes(2);

        // We resolve the name of the method
        this.name = ((ConstantUTF8) this.constantPool[dataInput.readUnsignedShort()]).getValue();

        this.methodSignature =
                new MethodSignature(((ConstantUTF8) this.constantPool[dataInput.readUnsignedShort()]).getValue());

        // The attributes
        int attributeCount = dataInput.readUnsignedShort();
        for (int i = 0; i < attributeCount; i++)
        {
            String attributeName = ((ConstantUTF8) this.constantPool[dataInput.readUnsignedShort()]).getValue();
            if (attributeName.equals("Code"))
            {
                this.readCodeAttribute(dataInput);
            }
            else
            {
                // unknown attribute. Let's skip the rest
                dataInput.skipBytes(dataInput.readInt());
            }
        }
    }

    /**
     * reads the codeAttribute and returns the new position.
     * @param dataInput DataInput; the dataInput
     * @throws IOException on failure
     */
    private void readCodeAttribute(final DataInput dataInput) throws IOException
    {
        // We skip the length
        dataInput.skipBytes(4);

        this.maxStack = dataInput.readUnsignedShort();
        this.localVariableTable = new LocalVariableDescriptor[dataInput.readUnsignedShort()];

        // Let's parse the actual code
        int codeLength = dataInput.readInt();
        List<Operation> code = new ArrayList<Operation>();
        List<Integer> positions = new ArrayList<Integer>();

        int position = 0;
        while (position < codeLength)
        {
            Operation operation = Interpreter.getFactory().readOperation(dataInput, position);
            code.add(operation);
            positions.add(position);
            position = position + operation.getByteLength();
        }
        if (position != codeLength)
        {
            throw new InterpreterException(" byteCode readmethod pos" + position + "!= codeL" + codeLength);
        }

        // Now we convert the lists to arrays.
        this.operations = code.toArray(new Operation[code.size()]);
        this.bytePositions = new int[positions.size()];

        int line = 0;
        for (Iterator<Integer> i = positions.iterator(); i.hasNext();)
        {
            this.bytePositions[line] = i.next().intValue();
            line++;
        }

        // Now we parse the exceptionTable
        this.exceptionTable = new ExceptionEntry[dataInput.readUnsignedShort()];
        for (int i = 0; i < this.exceptionTable.length; i++)
        {
            this.exceptionTable[i] = new ExceptionEntry(dataInput, this.constantPool);
        }

        // The attributes
        int attributeCount = dataInput.readUnsignedShort();
        for (int i = 0; i < attributeCount; i++)
        {
            String attributeName = ((ConstantUTF8) this.constantPool[dataInput.readUnsignedShort()]).getValue();
            if (attributeName.equals("LineNumberTable"))
            {
                this.readLineNumberTable(dataInput);
            }
            else if (attributeName.equals("LocalVariableTable"))
            {
                this.readLocalVariableTable(dataInput);
            }
            else
            {
                // unknown attribute. Let's skip the rest
                dataInput.skipBytes(dataInput.readInt());
            }
        }
    }

    /**
     * reads the localVariableTable.
     * @param dataInput DataInput; the dataInput to read
     * @throws IOException on failure
     */
    private void readLineNumberTable(final DataInput dataInput) throws IOException
    {
        // We skip the length(u4)
        dataInput.skipBytes(4);

        this.lineNumberTable = new LineNumber[dataInput.readUnsignedShort()];
        for (int i = 0; i < this.lineNumberTable.length; i++)
        {
            this.lineNumberTable[i] = new LineNumber(dataInput);
        }
    }

    /**
     * reads the localVariableTable.
     * @param dataInput DataInput; the dataInput to read
     * @throws IOException on failure
     */
    private void readLocalVariableTable(final DataInput dataInput) throws IOException
    {
        // We skip the length(u4)
        dataInput.skipBytes(4);

        int variables = dataInput.readUnsignedShort();
        for (int i = 0; i < variables; i++)
        {
            LocalVariableDescriptor variable = new LocalVariableDescriptor(dataInput, this.constantPool);
            this.localVariableTable[variable.getIndex()] = variable;
        }
    }
}
