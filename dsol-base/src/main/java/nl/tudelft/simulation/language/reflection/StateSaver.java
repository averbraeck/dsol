package nl.tudelft.simulation.language.reflection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.djutils.reflection.ClassUtil;

import nl.tudelft.simulation.language.DSOLException;

/**
 * StateSaver can serialize a full state of a single object, including the fields of its superclass, to an object. Later, this
 * object can be used to reset the full state of the object to the old value. This is useful when doing rollback in a
 * simulation; the state of objects can be rolled back to their old values, including random number generators, which will reset
 * their seed to the old value. Be careful with objects with shared pointers to e.g., collections, as the restoreState() on a
 * single object might lead to the duplication of the shared objects indicated by these pointers. When the rollback is needed
 * for an entire simulation, use XmlUtil, GSON, or another library to serialize / deserialize an entire simulation model at
 * once. <br>
 * <p>
 * Copyright (c) 2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See for
 * project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public final class StateSaver
{
    /** */
    private StateSaver()
    {
        // utility class
    }

    /**
     * Save the state of a single object into another object. Fields of the superclass are included. The state save is a deep
     * copy, using the writeObject() method of serialization.
     * @param object Object; the object to save the state from
     * @return the state packed into a memory object
     * @throws DSOLException on serialization error
     */
    public static byte[] saveState(final Object object) throws DSOLException
    {
        byte[] state = null;
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            state = bos.toByteArray();
            out.close();
            bos.close();
        }
        catch (IOException exception)
        {
            throw new DSOLException(exception);
        }
        return state;
    }

    /**
     * Retrieve the earlier saved state of a single object and write it into an object (which could be a clone or the original
     * object). Fields of the superclass are included. The state retrieval uses all information from a deep copy, using the
     * readObject() method of deserialization to fill the target object.
     * @param target Object; the target object to write the deserialized information into
     * @param state Object; the earlier saved state to write
     * @throws DSOLException on deserialization error
     */
    public static void restoreState(final Object target, final byte[] state) throws DSOLException
    {
        try
        {
            byte[] byteState = state;
            ByteArrayInputStream bis = new ByteArrayInputStream(byteState);
            ObjectInputStream in = new ObjectInputStream(bis);
            Object tempObject = in.readObject();
            Set<Field> allFields = ClassUtil.getAllFields(target.getClass());
            for (Field field : allFields)
            {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
                {
                    field.setAccessible(true);
                    field.set(target, field.get(tempObject));
                }
            }
            in.close();
            bis.close();
        }
        catch (IOException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException
                | SecurityException exception)
        {
            throw new DSOLException(exception);
        }
    }

}
