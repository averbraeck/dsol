package nl.tudelft.simulation.dsol.naming.context;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import nl.tudelft.simulation.naming.context.FileContextFactory;
import nl.tudelft.simulation.naming.context.JVMContextFactory;
import nl.tudelft.simulation.naming.context.RemoteContextFactory;
import nl.tudelft.simulation.naming.context.event.InitialEventContext;

/**
 * ContextTestUtil.java.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public final class ContextTestUtil
{
    /** */
    private ContextTestUtil()
    {
        // utility class
    }

    /**
     * Destroy the initialEventContext so next tests are not confronted with an already existing initial event context.
     * @param ctx InitialEventContext; the context to destroy
     */
    public static void destroyInitialEventContext(final InitialEventContext ctx)
    {
        if (ctx == null)
        {
            return;
        }
        try
        {
            // get the instance objects INSTANCE and defaultInitCtx, and set to null
            Field instance = InitialEventContext.class.getDeclaredField("INSTANCE");
            instance.setAccessible(true);
            instance.set(null, null);
            instance.setAccessible(false);
            
            Field defaultInitCtx = InitialEventContext.class.getDeclaredField("defaultInitCtx");
            defaultInitCtx.setAccessible(true);
            defaultInitCtx.set(ctx, null);
            defaultInitCtx.setAccessible(false);
            
            Field gotDefault = InitialEventContext.class.getDeclaredField("gotDefault");
            gotDefault.setAccessible(true);
            gotDefault.set(ctx, false);
            gotDefault.setAccessible(false);
            
            Field contextEventProducerImpl = InitialEventContext.class.getDeclaredField("contextEventProducerImpl");
            contextEventProducerImpl.setAccessible(true);
            contextEventProducerImpl.set(ctx, null);
            contextEventProducerImpl.setAccessible(false);
            
            // clean the static fields in the context factories
            Field jvmContext = JVMContextFactory.class.getDeclaredField("context");
            jvmContext.setAccessible(true);
            jvmContext.set(null, null);
            jvmContext.setAccessible(false);
           
            Field fileContext = FileContextFactory.class.getDeclaredField("context");
            fileContext.setAccessible(true);
            fileContext.set(null, null);
            fileContext.setAccessible(false);
           
            Field remoteContext = RemoteContextFactory.class.getDeclaredField("context");
            remoteContext.setAccessible(true);
            remoteContext.set(null, null);
            remoteContext.setAccessible(false);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
}
