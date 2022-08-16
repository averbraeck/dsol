package nl.tudelft.simulation.dsol.interpreter;

import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;
import nl.tudelft.simulation.dsol.interpreter.classfile.MethodDescriptor;

/**
 * A code Frame. See <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.6">
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.6 </a> for more information.
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
public class Frame implements Cloneable
{
    /** the constantPool of this frame. */
    private final Constant[] constantPool;

    /** the localVariables of this frame. */
    private final LocalVariable[] localVariables;

    /** the operandStack of this frame. */
    private final OperandStack operandStack;

    /** is this frame paused. */
    private boolean paused = false;

    /** the returnPosition refers the position in the operation[] to invoke on return. */
    private int returnPosition = 0;

    /** the operations in the frame. */
    private final Operation[] operations;

    /** the methodDescriptor of the frame. */
    private final MethodDescriptor methodDescriptor;

    /**
     * constructs a new Frame.
     * @param constantPool Constant[]; the constantPool
     * @param localVariables LocalVariable[]; the localVariables
     * @param operations Operation[]; the array of operations to execute
     * @param stack OperandStack; the stack
     * @param methodDescriptor MethodDescriptor; the methodDescriptor
     */
    public Frame(final Constant[] constantPool, final LocalVariable[] localVariables, final Operation[] operations,
            final OperandStack stack, final MethodDescriptor methodDescriptor)
    {
        super();
        this.constantPool = constantPool;
        this.localVariables = localVariables;
        this.operations = operations;
        this.operandStack = stack;
        this.methodDescriptor = methodDescriptor;
    }

    /**
     * @return Returns the constantPool.
     */
    public Constant[] getConstantPool()
    {
        return this.constantPool;
    }

    /**
     * @return Returns the localVariables.
     */
    public LocalVariable[] getLocalVariables()
    {
        return this.localVariables;
    }

    /**
     * @return Returns the returnPosition.
     */
    public int getReturnPosition()
    {
        return this.returnPosition;
    }

    /**
     * @return Returns the operations.
     */
    public Operation[] getOperations()
    {
        return this.operations;
    }

    /**
     * @param returnPosition int; The returnPosition to set.
     */
    public void setReturnPosition(final int returnPosition)
    {
        this.returnPosition = returnPosition;
    }

    /**
     * @return Returns the methodDescriptor.
     */
    public MethodDescriptor getMethodDescriptor()
    {
        return this.methodDescriptor;
    }

    /**
     * @return Returns the operandStack.
     */
    public OperandStack getOperandStack()
    {
        return this.operandStack;
    }

    /**
     * @return Returns whether the frame is paused.
     */
    public boolean isPaused()
    {
        return this.paused;
    }

    /**
     * @param paused boolean; The paused to set.
     */
    public void setPaused(final boolean paused)
    {
        this.paused = paused;
    }

    /** {@inheritDoc} */
    @Override
    public Object clone()
    {
        LocalVariable[] variables = new LocalVariable[this.localVariables.length];
        for (int i = 0; i < variables.length; i++)
        {
            variables[i] = (LocalVariable) this.localVariables[i].clone();
        }
        OperandStack newStack = (OperandStack) this.operandStack.clone();
        Frame frame = new Frame(this.constantPool, variables, this.operations, newStack, this.methodDescriptor);
        return frame;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        String result = "\n--- " + super.toString() + "---\n";
        result = result + "method:" + this.methodDescriptor.getMethod() + "\n";
        result = result + "invoker:" + this.localVariables[0].getValue() + "\n";
        return result;
    }
}
