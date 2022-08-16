package nl.tudelft.simulation.language.reflection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.djutils.reflection.ClassUtil;
import org.djutils.reflection.FieldSignature;

/**
 * A SerializableField.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class SerializableField implements Serializable
{
    /** the field. */
    private Field field = null;

    /**
     * constructs a new SerializableField.
     * @param field Field; The field
     */
    public SerializableField(final Field field)
    {
        super();
        this.field = field;
    }

    /**
     * constructs a new SerializableField.
     * @param clazz Class&lt;?&gt;; the clazz this field is instance of
     * @param fieldName String; the name of the field
     * @throws NoSuchFieldException whenever the field is not defined in clazz
     */
    public SerializableField(final Class<?> clazz, final String fieldName) throws NoSuchFieldException
    {
        this.field = ClassUtil.resolveField(clazz, fieldName);
    }

    /**
     * deserializes the field.
     * @return the Field
     */
    public Field deSerialize()
    {
        return this.field;
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
            out.writeObject(this.field.getDeclaringClass());
            out.writeObject(new FieldSignature(this.field.getName()));
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
            FieldSignature signature = (FieldSignature) in.readObject();
            this.field = ClassUtil.resolveField(declaringClass, signature.getStringValue());
        }
        catch (Exception exception)
        {
            throw new IOException(exception.getMessage());
        }
    }
}
