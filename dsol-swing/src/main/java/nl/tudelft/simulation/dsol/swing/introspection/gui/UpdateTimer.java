package nl.tudelft.simulation.dsol.swing.introspection.gui;

import java.awt.Component;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * provides a timed update mechanism for components. <br>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>.
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>.
 * @author Niels Lang.
 * @since 1.5
 */
public class UpdateTimer extends TimerTask
{
    /** the tables to update. */
    @SuppressWarnings("unchecked")
    private WeakReference<Component>[] components = new WeakReference[0];

    /** the timer. */
    private Timer timer = null;

    /** the period for this timer. */
    private long period = 300L;

    /**
     * constructs a new UpdateTimer.
     * @param period long; the period in milliseconds
     */
    public UpdateTimer(final long period)
    {
        super();
        this.period = period;
    }

    /**
     * adds a component to the list.
     * @param component Component; the component
     */
    @SuppressWarnings("unchecked")
    public synchronized void add(final Component component)
    {
        List<WeakReference<Component>> arrayList = new ArrayList<WeakReference<Component>>(Arrays.asList(this.components));
        arrayList.add(new WeakReference<Component>(component));
        this.components = arrayList.toArray(new WeakReference[arrayList.size()]);
        // The first table added
        if (this.timer == null)
        {
            this.timer = new Timer(true);
            this.timer.scheduleAtFixedRate(this, 0L, this.period);
        }
    }

    /**
     * removes a component from a list.
     * @param component Component; the component
     */
    public synchronized void remove(final Component component)
    {
        for (int i = (this.components.length - 1); i > -1; i--)
        {
            if (this.components[i].get().equals(component))
            {
                this.remove(this.components[i]);
            }
        }
    }

    /**
     * removes a reference from a list.
     * @param reference WeakReference&lt;Component&gt;; the reference
     */
    @SuppressWarnings("unchecked")
    private synchronized void remove(final WeakReference<Component> reference)
    {
        List<WeakReference<Component>> arrayList = new ArrayList<WeakReference<Component>>(Arrays.asList(this.components));
        arrayList.remove(reference);
        this.components = arrayList.toArray(new WeakReference[arrayList.size()]);
        if (this.components.length == 0)
        {
            // The last component is removed. Let's cancel the timer
            this.timer.cancel();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        for (int i = (this.components.length - 1); i > -1; i--)
        {
            Component component = this.components[i].get();
            if (component != null)
            {
                component.repaint();
            }
            else
            {
                this.remove(this.components[i]);
            }
        }
    }
}
