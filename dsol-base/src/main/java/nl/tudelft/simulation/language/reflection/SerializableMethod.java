package nl.tudelft.simulation.language.reflection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

import org.djutils.reflection.ClassUtil;
import org.djutils.reflection.MethodSignature;

/**
 * A SerializableMethod.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class SerializableMethod implements Serializable
{
    /** the method to use. */
    private Method method = null;

    /**
     * constructs a new SerializableMethod.
     * @param method the method
     */
    public SerializableMethod(final Method method)
    {
        super();
        this.method = method;
    }

    /**
     * constructs a new SerializableMethod.
     * @param clazz the clazz this field is instance of
     * @param methodName the name of the method
     * @param parameterTypes Class&lt;?&gt;..; The parameterTypes of the method
     * @throws NoSuchMethodException whenever the method is not defined in clazz
     */
    public SerializableMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes)
            throws NoSuchMethodException
    {
        this.method = ClassUtil.resolveMethod(clazz, methodName, parameterTypes);
    }

    /**
     * deserializes the field.
     * @return the Field
     */
    public Method deSerialize()
    {
        return this.method;
    }

    /**
     * writes a serializable method to stream.
     * @param out the outputstream
     * @throws IOException on IOException
     */
    private void writeObject(final ObjectOutputStream out) throws IOException
    {
        try
        {
            out.writeObject(this.method.getDeclaringClass());
            out.writeObject(this.method.getName());
            out.writeObject(new MethodSignature(this.method));
        }
        catch (Exception exception)
        {
            throw new IOException(exception.getMessage());
        }
    }

    /**
     * reads a serializable method from stream.
     * @param in java.io.ObjectInputStream; the inputstream
     * @throws IOException on IOException
     */
    private void readObject(final java.io.ObjectInputStream in) throws IOException
    {
        try
        {
            Class<?> declaringClass = (Class<?>) in.readObject();
            String methodName = (String) in.readObject();
            MethodSignature signature = (MethodSignature) in.readObject();
            this.method = ClassUtil.resolveMethod(declaringClass, methodName, signature.getParameterTypes());
        }
        catch (Exception exception)
        {
            throw new IOException(exception.getMessage());
        }
    }
}
