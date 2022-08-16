package nl.tudelft.simulation.naming.context;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.logger.CategoryLogger;

/**
 * The FileContext as a file-based implementation of the ContextInterface.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FileContext extends JVMContext
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20200101L;

    /** file links to the file. */
    private File file;

    /**
     * constructs a new FileContext.
     * @param file File; the file to write to
     * @param atomicName String; the name under which the root context will be registered
     */
    public FileContext(final File file, final String atomicName)
    {
        this(file, null, atomicName);
    }

    /**
     * constructs a new FileContext.
     * @param file File; the file to which to write
     * @param parent Context; the parent context
     * @param atomicName String; the atomicName
     */
    public FileContext(final File file, final ContextInterface parent, final String atomicName)
    {
        super(parent, atomicName);
        this.file = file;
    }

    /**
     * saves this object to file.
     * @throws NamingException on ioException
     * @throws RemoteException on network failure
     */
    private synchronized void save() throws NamingException, RemoteException
    {
        try
        {
            // TODO: check if non-serializability of the EventListeners is indeed needed (old version).
            // FileContext clone = (FileContext) this.clone();
            // clone.listeners.clear();
            ObjectOutputStream stream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(this.file)));
            stream.writeObject(this);
            stream.close();
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception, "saving in FileContext failed");
            throw new NamingException(exception.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void bind(final String name, final Object value) throws NamingException, RemoteException
    {
        super.bind(name, value);
        save();
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface createSubcontext(final String name) throws NamingException, RemoteException
    {
        ContextInterface result = super.createSubcontext(name);
        save();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void destroySubcontext(final String name) throws NamingException, RemoteException
    {
        super.destroySubcontext(name);
        save();
    }

    /** {@inheritDoc} */
    @Override
    public void rebind(final String name, final Object value) throws NamingException, RemoteException
    {
        super.rebind(name, value);
        save();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void rename(final String nameOld, final String nameNew) throws NamingException, RemoteException
    {
        super.rename(nameOld, nameNew);
        save();
    }

    /** {@inheritDoc} */
    @Override
    public void unbind(final String name) throws NamingException, RemoteException
    {
        super.unbind(name);
        save();
    }

}
