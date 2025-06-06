package nl.tudelft.simulation.language.reflection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.djutils.reflection.ClassUtil;
import org.djutils.reflection.MethodSignature;

/**
 * A SerializableConstructor.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class SerializableConstructor implements Serializable
{
    /** the constructor. */
    private Constructor<?> constructor = null;

    /**
     * constructs a new SerializableConstructor.
     * @param constructor The constructor
     */
    public SerializableConstructor(final Constructor<?> constructor)
    {
        super();
        this.constructor = constructor;
    }

    /**
     * constructs a new SerializableConstructor.
     * @param clazz the clazz this field is instance of
     * @param parameterTypes the parameterTypes of the constructor
     * @throws NoSuchMethodException whenever the method is not defined in clazz
     */
    public SerializableConstructor(final Class<?> clazz, final Class<?>... parameterTypes) throws NoSuchMethodException
    {
        this.constructor = ClassUtil.resolveConstructor(clazz, parameterTypes);
    }

    /**
     * deserializes the field.
     * @return the Constructor
     */
    public Constructor<?> deSerialize()
    {
        return this.constructor;
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
            out.writeObject(this.constructor.getDeclaringClass());
            out.writeObject(new MethodSignature(this.constructor));
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
            MethodSignature signature = (MethodSignature) in.readObject();
            this.constructor = ClassUtil.resolveConstructor(declaringClass, signature.getParameterTypes());
        }
        catch (Exception exception)
        {
            throw new IOException(exception.getMessage());
        }
    }
}
