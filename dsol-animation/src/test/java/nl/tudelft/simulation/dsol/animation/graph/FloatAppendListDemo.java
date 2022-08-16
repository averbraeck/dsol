package nl.tudelft.simulation.dsol.animation.graph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Demo of the memory use and CPU time for the FloatAppendList.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class FloatAppendListDemo
{
    /** */
    private FloatAppendListDemo()
    {
        // utility class
    }

    /**
     * @param args not used
     */
    public static void main(final String[] args)
    {
        int num = 10_000_000;
        long t = System.currentTimeMillis();
        FloatAppendList list = new FloatAppendList();
        for (int i = 0; i < num; i++)
        {
            list.add(i);
        }
        for (int i = 0; i < num; i++)
        {
            if (i != list.get(i))
            {
                System.err.println("get-error at " + i);
            }
        }
        int count = 0;
        for (float d : list)
        {
            if (count != d)
            {
                System.err.println("iterate-error at " + count);
            }
            count++;
        }
        System.out.println("msec FloatArrayList = " + (System.currentTimeMillis() - t));
        System.out.println("size FloatArrayList = " + sizeOf(list));

        t = System.currentTimeMillis();
        ArrayList<Float> alist = new ArrayList<>();
        for (int i = 0; i < num; i++)
        {
            alist.add((float) i);
        }
        for (int i = 0; i < num; i++)
        {
            if (i != alist.get(i))
            {
                System.err.println("get-error at " + i);
            }
        }
        count = 0;
        for (float d : alist)
        {
            if (count != d)
            {
                System.err.println("iterate-error at " + count);
            }
            count++;
        }
        System.out.println("msec      ArrayList = " + (System.currentTimeMillis() - t));
        System.out.println("size      ArrayList = " + sizeOf(alist));
    }

    /**
     * Calculate the size of a complete (serializable) object, including its dependencies.
     * @param object the object to check
     * @return the size in bytes
     */
    private static long sizeOf(final Serializable object)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            return baos.size();
        }
        catch (IOException ioe)
        {
            return -1;
        }
    }

}
