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
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class SerializableConstructor implements Serializable
{
    /** the constructor. */
    private Constructor<?> constructor = null;

    /**
     * constructs a new SerializableConstructor.
     * @param constructor Constructor&lt;?&gt;; The constructor
     */
    public SerializableConstructor(final Constructor<?> constructor)
    {
        super();
        this.constructor = constructor;
    }

    /**
     * constructs a new SerializableConstructor.
     * @param clazz Class&lt;?&gt;; the clazz this field is instance of
     * @param parameterTypes Class&lt;?&gt;...; the parameterTypes of the constructor
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
     * @param out ObjectOutputStream; the outputstream
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
