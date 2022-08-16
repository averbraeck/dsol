package nl.tudelft.simulation.dsol.interpreter.operations.reflection;

import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.djutils.primitives.Primitive;
import org.djutils.reflection.ClassUtil;
import org.djutils.reflection.MethodSignature;

import nl.tudelft.simulation.dsol.interpreter.Frame;
import nl.tudelft.simulation.dsol.interpreter.Interpreter;
import nl.tudelft.simulation.dsol.interpreter.InterpreterException;
import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.classfile.ConstantMethodref;
import nl.tudelft.simulation.dsol.interpreter.operations.InvokeOperation;
import nl.tudelft.simulation.dsol.interpreter.operations.NEW;

/**
 * INVOKESPECIAL.
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
public class INVOKESPECIAL extends InvokeOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 183;

    /** the index to load. */
    private final int index;

    /**
     * constructs a new INVOKESPECIAL.
     * @param dataInput DataInput; the dataInput
     * @throws IOException on IOfailure
     */
    public INVOKESPECIAL(final DataInput dataInput) throws IOException
    {
        super();
        this.index = dataInput.readUnsignedShort();
    }

    /** {@inheritDoc} */
    @Override
    public Frame execute(final Frame frame)
    {
        try
        {
            ConstantMethodref constantMethodref = (ConstantMethodref) frame.getConstantPool()[this.index];
            Class<?>[] parameterTypes =
                    new MethodSignature(constantMethodref.getConstantNameAndType().getDescriptor()).getParameterTypes();
            String methodName = constantMethodref.getConstantNameAndType().getName();
            if (methodName.equals("<init>"))
            {
                Object[] args = new Object[parameterTypes.length];
                for (int i = args.length - 1; i > -1; i--)
                {
                    args[i] = Primitive.cast(parameterTypes[i], frame.getOperandStack().pop());
                }
                Object objectRef = frame.getOperandStack().pop();
                Class<?> instanceClass = ((NEW.UninitializedInstance) objectRef).getInstanceClass();
                Constructor<?> constructor = ClassUtil.resolveConstructor(instanceClass, parameterTypes);
                constructor.setAccessible(true);
                Object result = constructor.newInstance(args);
                frame.getOperandStack().replace(objectRef, result);
                LocalVariable.replace(frame.getLocalVariables(), objectRef, result);

                if (Interpreter.DEBUG)
                {
                    System.out.println("  " + instanceClass.getSimpleName() + ".<init>");
                }

                return null;
            }

            Object objectRef = frame.getOperandStack().peek(parameterTypes.length);

            // Now the normal methods
            Method method = null;

            // look if we need to resolve a super() method
            Class<?> referenceClass = constantMethodref.getConstantClass().getValue().getClassValue();
            if (objectRef.getClass().getSuperclass().equals(referenceClass))
            {
                method = ClassUtil.resolveMethod(objectRef.getClass().getSuperclass(), methodName, parameterTypes);
            }
            else
            {
                method = ClassUtil.resolveMethod(objectRef, methodName, parameterTypes);
            }

            synchronized (frame.getOperandStack())
            {
                // Let's create the arguments
                Object[] args = new Object[parameterTypes.length];
                for (int i = args.length - 1; i > -1; i--)
                {
                    args[i] = Primitive.cast(parameterTypes[i], frame.getOperandStack().pop());
                }
                frame.getOperandStack().pop();
                return this.execute(frame, objectRef, method, args);
            }
        }
        catch (Exception exception)
        {
            throw new InterpreterException(exception);
        }
    }

    /**
     * executes the method on the objectRef.
     * @param frame Frame; the frame
     * @param objectRef Object; the objectRef
     * @param method Method; the method
     * @param arguments Object[]; the arguments
     * @return the resulting Frame
     * @throws Exception on reflection exception
     */
    public Frame execute(final Frame frame, final Object objectRef, final Method method, final Object[] arguments)
            throws Exception
    {
        if (Interpreter.DEBUG)
        {
            System.out.println("  invoke " + objectRef.getClass().getSimpleName() + "." + method.getName());
        }

        method.setAccessible(true);
        Object result = null;
        try
        {
            result = method.invoke(objectRef, arguments);
        }
        catch (Exception exception)
        {
            frame.getOperandStack().push(exception.getCause());
            throw exception;
        }

        // Let's see what to do with the stack
        if (!method.getReturnType().equals(void.class))
        {
            frame.getOperandStack().push(result);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getByteLength()
    {
        return OPCODE_BYTE_LENGTH + 2;
    }

    /** {@inheritDoc} */
    @Override
    public int getOpcode()
    {
        return INVOKESPECIAL.OP;
    }
}
