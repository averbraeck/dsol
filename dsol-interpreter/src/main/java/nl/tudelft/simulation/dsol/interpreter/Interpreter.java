package nl.tudelft.simulation.dsol.interpreter;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import org.djutils.io.URLResource;
import org.djutils.logger.CategoryLogger;
import org.djutils.reflection.ClassUtil;

import nl.tudelft.simulation.dsol.interpreter.classfile.ClassDescriptor;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;
import nl.tudelft.simulation.dsol.interpreter.classfile.ExceptionEntry;
import nl.tudelft.simulation.dsol.interpreter.classfile.LineNumber;
import nl.tudelft.simulation.dsol.interpreter.classfile.MethodDescriptor;
import nl.tudelft.simulation.dsol.interpreter.operations.ATHROW;
import nl.tudelft.simulation.dsol.interpreter.operations.FactoryInterface;
import nl.tudelft.simulation.dsol.interpreter.operations.InterpreterFactory;
import nl.tudelft.simulation.dsol.interpreter.operations.InvokeOperation;
import nl.tudelft.simulation.dsol.interpreter.operations.JumpOperation;
import nl.tudelft.simulation.dsol.interpreter.operations.RET;
import nl.tudelft.simulation.dsol.interpreter.operations.RETURN;
import nl.tudelft.simulation.dsol.interpreter.operations.ReturnOperation;
import nl.tudelft.simulation.dsol.interpreter.operations.VoidOperation;
import nl.tudelft.simulation.dsol.interpreter.operations.WIDE;
import nl.tudelft.simulation.dsol.interpreter.operations.custom.InterpreterOracleInterface;

/**
 * The Java interpreter.
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
public final class Interpreter
{
    /** the cache. */
    private static final Map<AccessibleObject, Frame> CACHE = new LinkedHashMap<AccessibleObject, Frame>();

    /** the interpreter factory class name. */
    private static FactoryInterface interpreterFactory = null;

    /** DEBUG tells to print the bytecode statements to stdout */
    public static boolean DEBUG = false;

    static
    {
        FactoryInterface factory = new InterpreterFactory();
        try
        {
            Properties properties = new Properties();
            properties.load(URLResource.getResourceAsStream("/resources/interpreter.properties"));

            Class<?> factoryClass = Class.forName(properties.getProperty("interpreter.operation.factory"));
            if (properties.getProperty("interpreter.operation.oracle") != null)
            {
                InterpreterOracleInterface oracle = (InterpreterOracleInterface) Class
                        .forName(properties.getProperty("interpreter.operation.oracle")).newInstance();
                factory = (FactoryInterface) factoryClass.getConstructor(new Class[] {InterpreterOracleInterface.class})
                        .newInstance(new Object[] {oracle});
            }
            else
            {
                factory = (FactoryInterface) factoryClass.newInstance();
            }
        }
        catch (Exception exception)
        {
            // we default
            CategoryLogger.always().error(exception);
        }
        Interpreter.setFactory(factory);
    }

    /**
     * sets the Interpreter factory.
     * @param factory FactoryInterface; the factory to use
     */
    public static void setFactory(final FactoryInterface factory)
    {
        Interpreter.interpreterFactory = factory;
    }

    /**
     * @return interpreterFactory
     */
    public static FactoryInterface getFactory()
    {
        return interpreterFactory;
    }

    /**
     * constructs a new Interpreter.
     */
    private Interpreter()
    {
        // unreachable code
    }

    /**
     * creates a frame for a method.
     * @param object Object; the object on which the method must be invoked
     * @param method AccessibleObject; the method or constructor
     * @param arguments Object[]; the arguments
     * @return Frame the result
     * @throws ClassNotFoundException whenever the classpath is incomplete
     * @throws IOException on IOException
     */
    public static Frame createFrame(final Object object, final AccessibleObject method, final Object[] arguments)
            throws ClassNotFoundException, IOException
    {
        Frame frame = null;
        Object[] args = new Object[0];
        if (arguments != null)
        {
            args = arguments;
        }
        if (Interpreter.CACHE.containsKey(method))
        {
            frame = (Frame) Interpreter.CACHE.get(method).clone();
        }
        else
        {
            ClassDescriptor classDescriptor = null;
            if (method instanceof Method)
            {
                classDescriptor = ClassDescriptor.get(((Method) method).getDeclaringClass());
            }
            else
            {
                classDescriptor = ClassDescriptor.get(((Constructor<?>) method).getDeclaringClass());
            }
            MethodDescriptor methodDescriptor = classDescriptor.getMethod(method);
            OperandStack operandStack = new OperandStack(methodDescriptor.getMaxStack());
            LocalVariable[] localVariables = LocalVariable.newInstance(methodDescriptor.getLocalVariableTable());
            frame = new Frame(classDescriptor.getConstantPool(), localVariables, methodDescriptor.getOperations(), operandStack,
                    methodDescriptor);
            Interpreter.CACHE.put(method, frame);
        }

        // If method!=static put object on localVariableTable
        int modifiers = -1;
        int counter = 0;
        Class<?>[] parameterTypes = null;
        if (method instanceof Method)
        {
            parameterTypes = ((Method) method).getParameterTypes();
            modifiers = ((Method) method).getModifiers();
        }
        else
        {
            parameterTypes = ((Constructor<?>) method).getParameterTypes();
            modifiers = ((Constructor<?>) method).getModifiers();
        }

        if (!Modifier.isStatic(modifiers))
        {
            frame.getLocalVariables()[counter++].setValue(object);
        }

        // add the call parameters for the method to the stack
        for (int i = 0; i < args.length; i++)
        {
            frame.getLocalVariables()[counter++].setValue(arguments[i]);
            if ((parameterTypes[i].equals(double.class)) || (parameterTypes[i].equals(long.class)))
            {
                counter++;
            }
        }

        if (Interpreter.DEBUG)
        {
            AccessibleObject m = frame.getMethodDescriptor().getMethod();
            String c = null;
            if (m instanceof Method)
                c = ((Method) m).getDeclaringClass().getSimpleName();
            else
                c = ((Constructor<?>) m).getDeclaringClass().getSimpleName();
            System.out.println("  createFrame " + c + "." + frame.getMethodDescriptor().getName());
        }

        return frame;
    }

    /**
     * throws an exception.
     * @param operation Operation; the aThrow operation to invoke
     * @param frame Frame; the frame to start with
     * @param frameStack Stack&lt;Frame&gt;; the framestack
     * @return the frame and operationIndex to continue with...
     */
    public static Frame aThrow(final Operation operation, final Frame frame, final Stack<Frame> frameStack)
    {
        ((ATHROW) operation).execute(frame);
        ExceptionEntry exceptionEntry = (ExceptionEntry) frame.getOperandStack().pop();
        if (exceptionEntry != null)
        {
            int operationIndex = frame.getMethodDescriptor().getOperationIndex(exceptionEntry.getHandler());
            frame.setReturnPosition(operationIndex);
            return frame;
        }

        // no handler is found. The exception is forwarded...
        Throwable throwable = (Throwable) frame.getOperandStack().pop();

        // First we destroy this frame
        frameStack.pop();

        // We take the caller and push
        if (frameStack.isEmpty())
        {
            throw new RuntimeException("\n----------------------\n" + throwable + ": " + frame.getLocalVariables()[0].getValue()
                    + "." + frame.getMethodDescriptor().getName() + "\n----------------------");
        }
        Frame newFrame = frameStack.peek();
        ((ATHROW) operation).setBytePosition(newFrame.getMethodDescriptor().getBytePosition(newFrame.getReturnPosition()));
        newFrame.getOperandStack().push(throwable);
        return Interpreter.aThrow(operation, newFrame, frameStack);
    }

    /**
     * interprets the frameStack.
     * @param frameStack Stack&lt;Frame&gt;; the frameStack of the interpreter
     * @return Object the return value of the invoked method
     */
    public static Object interpret(final Stack<Frame> frameStack)
    {
        Frame frame = frameStack.peek();
        OperandStack operandStack = frame.getOperandStack();
        Constant[] constantPool = frame.getConstantPool();
        LocalVariable[] localVariables = frame.getLocalVariables();
        MethodDescriptor methodDescriptor = frame.getMethodDescriptor();
        int operationIndex = frame.getReturnPosition();
        while (true)
        {
            Operation operation = frame.getOperations()[operationIndex];

            if (DEBUG)
            {
                // find the line number
                int bytePos = 0;
                for (int i = 0; i < operationIndex; i++)
                {
                    bytePos += frame.getOperations()[i].getByteLength();
                }
                int line = 0;
                if (frame.getMethodDescriptor().getLineNumberTable().length == 1)
                    line = frame.getMethodDescriptor().getLineNumberTable()[0].getLineNumber();
                for (int i = 0; i < frame.getMethodDescriptor().getLineNumberTable().length - 1; i++)
                {
                    LineNumber ln1 = frame.getMethodDescriptor().getLineNumberTable()[i];
                    LineNumber ln2 = frame.getMethodDescriptor().getLineNumberTable()[i + 1];
                    if (bytePos >= ln1.getStartByte() && bytePos < ln2.getStartByte())
                        line = ln1.getLineNumber();
                }
                // System.out.print(bytePos + ":");
                // for (LineNumber ln : frame.getMethodDescriptor().getLineNumberTable())
                // System.out.print(" " + ln.getLineNumber() + "=" + ln.getStartByte());
                // System.out.println();
                String methodName;
                if (frame.getMethodDescriptor().getMethod() instanceof Method)
                    methodName = ((Method) frame.getMethodDescriptor().getMethod()).getDeclaringClass().getSimpleName();
                else
                    methodName = ((Constructor<?>) frame.getMethodDescriptor().getMethod()).getDeclaringClass().getSimpleName();
                System.out.println(operation.getClass().getSimpleName() + " @ " + methodName + "."
                        + frame.getMethodDescriptor().getName() + "(" + line + ")");
            }

            // WIDE is special. We need to get its target
            if (operation instanceof WIDE)
            {
                operation = ((WIDE) operation).getTarget();
            }

            // ATHROW is special
            if (operation instanceof ATHROW)
            {
                frame = Interpreter.aThrow(operation, frame, frameStack);
                operandStack = frame.getOperandStack();
                constantPool = frame.getConstantPool();
                localVariables = frame.getLocalVariables();
                methodDescriptor = frame.getMethodDescriptor();
                operationIndex = frame.getReturnPosition();
                continue;
            }

            // Void operations are executed and done
            if (operation instanceof VoidOperation)
            {
                ((VoidOperation) operation).execute(operandStack, constantPool, localVariables);
                operationIndex++;
                continue;
            }

            // Invoke operations are executed and done
            if (operation instanceof InvokeOperation)
            {
                Frame childFrame = null;
                try
                {
                    childFrame = ((InvokeOperation) operation).execute(frame);
                }
                catch (Exception exception)
                {
                    frame.getOperandStack().push(exception);
                    frame = Interpreter.aThrow(new ATHROW(methodDescriptor.getBytePosition(operationIndex)), frame, frameStack);
                    operandStack = frame.getOperandStack();
                    constantPool = frame.getConstantPool();
                    localVariables = frame.getLocalVariables();
                    methodDescriptor = frame.getMethodDescriptor();
                    operationIndex = frame.getReturnPosition();
                    continue;
                }
                operationIndex++;
                if (childFrame != null)
                {
                    frame.setReturnPosition(operationIndex);
                    if (frame.isPaused())
                    {
                        return frame;
                    }
                    frame = childFrame;
                    frameStack.push(frame);
                    operandStack = frame.getOperandStack();
                    constantPool = frame.getConstantPool();
                    localVariables = frame.getLocalVariables();
                    methodDescriptor = frame.getMethodDescriptor();
                    operationIndex = 0;
                }
                continue;
            }

            // What to do with jumps
            if (operation instanceof JumpOperation)
            {
                int offset = ((JumpOperation) operation).execute(operandStack, constantPool, localVariables);
                int bytePosition = offset;
                if (!(operation instanceof RET))
                {
                    bytePosition = bytePosition + methodDescriptor.getBytePosition(operationIndex);
                }
                operationIndex = methodDescriptor.getOperationIndex(bytePosition);
                continue;
            }

            // Return operations are executed and returned
            if (operation instanceof ReturnOperation)
            {
                Object result = ((ReturnOperation) operation).execute(frame);

                // We destroy this frame
                frameStack.pop();

                // We take the caller and push
                if (frameStack.isEmpty())
                {
                    return result;
                }
                frame = frameStack.peek();
                operandStack = frame.getOperandStack();
                constantPool = frame.getConstantPool();
                localVariables = frame.getLocalVariables();
                methodDescriptor = frame.getMethodDescriptor();
                operationIndex = frame.getReturnPosition();
                if (!(operation instanceof RETURN))
                {
                    operandStack.push(result);
                }
            }
        }
    }

    /**
     * interprets the invocation of a method on an object.
     * @param object Object; the object on which the method must be invoked
     * @param methodName String; the methodName
     * @param arguments Object[]; the arguments
     * @param argumentTypes Class&lt;?&gt;[]; the classes of the arguments
     * @return Object the result
     */
    public static Object invoke(final Object object, final String methodName, final Object[] arguments,
            final Class<?>[] argumentTypes)
    {
        try
        {
            AccessibleObject method = null;
            if (!methodName.equals("<init>"))
            {
                if (object instanceof Class)
                {
                    method = ClassUtil.resolveMethod((Class<?>) object, methodName, argumentTypes);
                }
                else
                {
                    method = ClassUtil.resolveMethod(object.getClass(), methodName, argumentTypes);
                }
            }
            else
            {
                method = object.getClass().getDeclaredConstructor(argumentTypes);
            }
            return Interpreter.invoke(object, method, arguments);
        }
        catch (Exception exception)
        {
            throw new InterpreterException(exception);
        }
    }

    /**
     * interprets the invocation of a method on an object.
     * @param object Object; the object on which the method must be invoked
     * @param method AccessibleObject; the method
     * @param arguments Object[]; the arguments
     * @return Object the result
     */
    public static Object invoke(final Object object, final AccessibleObject method, final Object[] arguments)
    {
        if (DEBUG)
        {
            String methodName;
            if (method instanceof Method)
                methodName = ((Method) method).getName();
            else
                methodName = ((Constructor<?>) method).getName();
            System.out.println("  invoke " + object.getClass().getSimpleName() + "." + methodName);
        }

        try
        {
            if (method instanceof Constructor && Modifier.isNative(((Constructor<?>) method).getModifiers()))
            {
                return ((Constructor<?>) method).newInstance(arguments);
            }
            if (method instanceof Method && Modifier.isNative(((Method) method).getModifiers()))
            {
                return ((Method) method).invoke(object, arguments);
            }
            Stack<Frame> frameStack = new Stack<Frame>();
            frameStack.push(Interpreter.createFrame(object, method, arguments));
            // Now we interpret
            return Interpreter.interpret(frameStack);
        }
        catch (Exception exception)
        {
            throw new InterpreterException(exception);
        }
    }

}
