package nl.tudelft.simulation.naming.context.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import nl.tudelft.simulation.naming.context.ContextInterface;

/**
 * ContextUtil contains a few helper methods to deal with an InitialEventContext.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ContextUtil
{
    /**
     * Lookup or create a sub-context in the parentContext with the name as its path. The path can be absolute or relative. The
     * terminating part of the name will be used as the key under which the created subcontext will be registered.
     * @param parentContext ContextInterface; the parent context
     * @param name String; the name to register the new subcontext
     * @return ContextInterface; the newly created subcontext
     * @throws NamingException when terminating key in the name is blank or contains "/" character(s)
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    public static ContextInterface lookupOrCreateSubContext(final ContextInterface parentContext, final String name)
            throws NamingException, RemoteException
    {
        try
        {
            if (parentContext.exists(name))
            {
                Object result = parentContext.get(name);
                if (result instanceof ContextInterface)
                    return (ContextInterface) result;
                throw new NamingException(
                        "lookup for " + name + " in context " + parentContext + " returned object that is not a Context");
            }
        }
        catch (NameNotFoundException nnfe)
        {
            // ignore -- create the context path
        }
        return parentContext.createSubcontext(name);
    }

    /**
     * Lookup a sub-context in the parentContext with the name as its path. The path can be absolute or relative. The
     * terminating part of the name will be used as the key under which the created subcontext will be registered.
     * @param parentContext ContextInterface; the parent context
     * @param name String; the name to register the new subcontext
     * @return ContextInterface; the newly created subcontext
     * @throws NamingException when terminating key in the name is blank or contains "/" character(s)
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    public static ContextInterface lookupSubContext(final ContextInterface parentContext, final String name)
            throws NamingException, RemoteException
    {
        if (parentContext.exists(name))
        {
            Object result = parentContext.get(name);
            if (result instanceof ContextInterface)
                return (ContextInterface) result;
            throw new NamingException(
                    "lookup for " + name + " in context " + parentContext + " returned object that is not a Context");
        }
        throw new NamingException("Context " + name + " not found in parentContext " + parentContext);
    }

    /**
     * Destroy a sub-context in the parentContext with the name as its path. The path can be absolute or relative. The
     * terminating part of the name will be used as the key under for the subcontext to be removed.
     * @param parentContext ContextInterface; the parent context
     * @param name String; the name to use to find the subcontext to remove
     * @throws NamingException when terminating key in the name is blank or contains "/" character(s)
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    public static void destroySubContext(final ContextInterface parentContext, final String name)
            throws NamingException, RemoteException
    {
        parentContext.destroySubcontext(name);
    }

    /**
     * Resolve the key(s) for an object for a given context. This can be an expensive operation if the context is large. An
     * object can be registered zero or more times in the context, so a List of keys under which the object is registered will
     * be returned. The keys are relative to the startContext. The method starts with the given context. It is possible to look
     * up null objects in the Context.
     * @param startContext ContextInterface; the context to start the search
     * @param object Object; the object to look up in the tree under the startContext
     * @return List&lt;String&gt;; the list of keys that are bound to the object, or an empty list if no bindings for the object
     *         were found
     * @throws NamingException when an error occurs during searching
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    public static List<String> resolveKeys(final ContextInterface startContext, final Object object)
            throws NamingException, RemoteException
    {
        List<String> result = new ArrayList<>();
        resolveKeys(startContext, object, result, "");
        return result;
    }

    /**
     * Resolve the key(s) for an object for a given context. This can be an expensive operation if the context is large. An
     * object can be registered zero or more times in the context, so a List of keys under which the object is registered will
     * be returned. The keys are relative to the startContext. The method starts with the given context. It is possible to look
     * up null objects in the Context.
     * @param context ContextInterface; the context to start the search
     * @param object Object; the object to look up in the tree under the startContext
     * @param result List&lt;String&gt;; the current list of keys that are bound to the object, or an empty list if no bindings
     *            for the object were found yet
     * @param partialKey the key of the current level in the tree, relative to the original start context of the search
     * @throws NamingException when an error occurs during searching
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    private static void resolveKeys(final ContextInterface context, final Object object, final List<String> result,
            final String partialKey) throws NamingException, RemoteException
    {
        for (Entry<String, Object> binding : context.bindings().entrySet())
        {
            Object value = binding.getValue();
            if (value instanceof ContextInterface)
            {
                resolveKeys((ContextInterface) value, object, result,
                        partialKey + ContextInterface.SEPARATOR + binding.getKey());
            }
            // no else; we might be looking for an object that implements ContextInterface
            if ((value == null && object == null) || (value != null && value.equals(object)))
            {
                result.add(partialKey + ContextInterface.SEPARATOR + binding.getKey());
            }
        }
    }

    /**
     * recursively print the context in human-readable format to a String.
     * @param ctx the context to print
     * @return a human-readable String with the context tree
     */
    public static String toText(final ContextInterface ctx)
    {
        return toText(ctx, new StringBuffer(), 0);
    }

    /**
     * recursively print the context in human-readable format to a String.
     * @param ctx the context to print
     * @param sb the StringBuffer to add the information to
     * @param indent the indentation on the screen
     * @return a human-readable String with the context tree
     */
    private static String toText(final ContextInterface ctx, final StringBuffer sb, final int indent)
    {
        try
        {
            if (indent > 0)
            {
                sb.append(String.join("", Collections.nCopies(indent - 1, "| ")));
                sb.append("+ ");
            }
            sb.append("CTX ");
            sb.append(ctx.getAtomicName());
            sb.append("\n");
            for (String key : ctx.keySet())
            {
                Object obj = ctx.getObject(key);
                if (obj instanceof ContextInterface)
                {
                    toText((ContextInterface) obj, sb, indent + 1);
                }
                else
                {
                    sb.append(String.join("", Collections.nCopies(indent, "| ")));
                    sb.append("+ ");
                    sb.append(key);
                    sb.append("=");
                    sb.append(obj);
                    sb.append("\n");
                }
            }
        }
        catch (NamingException | RemoteException exception)
        {
            sb.append("ERR " + exception.getMessage() + "\n");
        }
        return sb.toString();
    }

}
