package nl.tudelft.simulation.dsol.animation.graph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Demo of the memory use and CPU time for the DoubleAppendList.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public final class DoubleAppendListDemo
{
    /** */
    private DoubleAppendListDemo()
    {
        // utility constructor
    }

    /**
     * @param args not used
     */
    public static void main(final String[] args)
    {
        int num = 10_000_000;
        long t = System.currentTimeMillis();
        DoubleAppendList list = new DoubleAppendList();
        for (int i = 0; i < num; i++)
        {
            list.add(i);
        }
        for (int i = 0; i < num; i++)
        {
            if (i != list.get(i))
            { System.err.println("get-error at " + i); }
        }
        int count = 0;
        for (double d : list)
        {
            if (count != d)
            { System.err.println("iterate-error at " + count); }
            count++;
        }
        System.out.println("msec DoubleArrayList = " + (System.currentTimeMillis() - t));
        System.out.println("size DoubleArrayList = " + sizeOf(list));

        t = System.currentTimeMillis();
        ArrayList<Double> alist = new ArrayList<>();
        for (int i = 0; i < num; i++)
        {
            alist.add((double) i);
        }
        for (int i = 0; i < num; i++)
        {
            if (i != alist.get(i))
            { System.err.println("get-error at " + i); }
        }
        count = 0;
        for (double d : alist)
        {
            if (count != d)
            { System.err.println("iterate-error at " + count); }
            count++;
        }
        System.out.println("msec       ArrayList = " + (System.currentTimeMillis() - t));
        System.out.println("size       ArrayList = " + sizeOf(alist));
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
