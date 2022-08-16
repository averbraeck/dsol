
package nl.tudelft.simulation.dsol.interpreter;

import java.rmi.RemoteException;

/**
 * The InterpreterTest.
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
public final class InterpreterTest
{
    /**
     * constructs a new InterpreterTest.
     */
    private InterpreterTest()
    {
        super();
        // unreachable code
    }

    /**
     * we do method c.
     */
    public void doC()
    {
        try
        {
            throw new RuntimeException("exception 1");
        }
        catch (RuntimeException runtimeException)
        {
            System.out.println(runtimeException.getMessage());
        }
        System.out.println("We succesfully passed the test");
        try
        {
            throw new RuntimeException("exception 2");
        }
        catch (RuntimeException runtimeException)
        {
            System.out.println(runtimeException.getMessage());
        }
    }

    /**
     * we do method b.
     * @throws RemoteException to trigger a remoteException
     */
    public void doB() throws RemoteException
    {
        System.out.println("Peter");
        throw new RemoteException("A remoteException");
    }

    /**
     * we do method a.
     */
    public void doA()
    {
        try
        {
            try
            {
                throw new RuntimeException("hoi");
            }
            catch (Exception e)
            {
                this.doB();
            }
        }
        catch (RemoteException r1)
        {
            System.out.println("R1");
            throw new IllegalStateException();
        }
    }

    /**
     * executes the application.
     * @param args the commandline arguments
     */
    public static void main(final String[] args)
    {
        try
        {
            Interpreter.invoke(new InterpreterTest(), "doC", null, null);
            Interpreter.invoke(new InterpreterTest(), "doA", null, null);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
