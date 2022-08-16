package nl.tudelft.simulation.dsol.swing.gui.animation.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.djutils.event.Event;
import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;
import org.djutils.event.EventProducer;
import org.djutils.event.EventProducerInterface;
import org.djutils.event.EventType;
import org.djutils.event.EventTypeInterface;
import org.djutils.event.ref.ReferenceType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControl;
import nl.tudelft.simulation.dsol.swing.gui.util.Icons;

/**
 * A search panel for a single object type. The Search panel allows to search for an object with a certain Id, for which the
 * code to locate the object must be specified. The code is based on OpenTrafficSim project component with the same purpose.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class SearchPanel extends JPanel implements ActionListener, FocusListener, DocumentListener, EventProducerInterface
{
    /** */
    private static final long serialVersionUID = 20200127L;

    /** The type-of-object-to-search-for selector. */
    private JComboBox<ObjectKind<?>> typeToSearch;

    /** The type-of-object-to-search-for selector. */
    private final List<ObjectKind<?>> objectKindList;

    /** Id of the object to search for. */
    private final JTextField idTextField;

    /** Track object check box. */
    private final JCheckBox trackObject;

    /** delegate class to do handle event producing. */
    private final SearchPanelEventProducer searchPanelEventProducer;

    /** the event fired when a new search object is selected by the user. */
    public static final EventType ANIMATION_SEARCH_OBJECT_EVENT =
            new EventType(new MetaData("ANIMATION_SEARCH_OBJECT_EVENT", "ANIMATION_SEARCH_OBJECT_EVENT",
                    new ObjectDescriptor("objectKind", "object kind key", SearchPanel.ObjectKind.class),
                    new ObjectDescriptor("objectId", "object id", String.class),
                    new ObjectDescriptor("track", "track the object or not", Boolean.class)));

    /**
     * Construct a new search panel.
     */
    public SearchPanel()
    {
        this.searchPanelEventProducer = new SearchPanelEventProducer();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(new JLabel("    ")); // insert some white space in the GUI
        this.add(new JLabel(Icons.loadIcon("/resources/Search.png")));
        this.objectKindList = new ArrayList<>();
        this.typeToSearch = new JComboBox<ObjectKind<?>>(new ObjectKind<?>[] {});
        this.add(this.typeToSearch);

        /** Text field with appearance control. */
        class AppearanceControlTextField extends JTextField implements AppearanceControl
        {
            /** */
            private static final long serialVersionUID = 20180207L;

            /** {@inheritDoc} */
            @Override
            public boolean isForeground()
            {
                return false;
            }

            /** {@inheritDoc} */
            @Override
            public boolean isBackground()
            {
                return false;
            }

            /** {@inheritDoc} */
            @Override
            public String toString()
            {
                return "AppearanceControlLabel []";
            }
        }

        this.idTextField = new AppearanceControlTextField();
        this.idTextField.setPreferredSize(new Dimension(100, 0));
        this.add(this.idTextField);
        this.trackObject = new JCheckBox("track");
        this.add(this.trackObject);
        this.trackObject.setActionCommand("Tracking status changed");
        this.idTextField.setActionCommand("Id changed");
        this.typeToSearch.setActionCommand("Type changed");
        this.trackObject.addActionListener(this);
        this.idTextField.addActionListener(this);
        this.typeToSearch.addActionListener(this);
        this.idTextField.addFocusListener(this);
        this.idTextField.getDocument().addDocumentListener(this);
    }

    /**
     * Add an object kind to search for.
     * @param objectKind ObjectKind&lt;?&gt;; the objectKind to add to the list of searcheable objects.
     */
    public void addObjectKind(final ObjectKind<?> objectKind)
    {
        this.objectKindList.add(objectKind);
        this.typeToSearch = new JComboBox<ObjectKind<?>>(this.objectKindList.toArray(new ObjectKind[] {}));
        this.typeToSearch.invalidate();
    }

    /**
     * Update all values at once.
     * @param objectKey String; key of the object type to search
     * @param objectId String; id of object to search
     * @param track boolean; if true; track continuously; if false; center on it, but do not track
     */
    public void selectAndTrackObject(final String objectKey, final String objectId, final boolean track)
    {
        for (int index = this.typeToSearch.getItemCount(); --index >= 0;)
        {
            if (this.typeToSearch.getItemAt(index).getKey().equals(objectKey))
            {
                this.typeToSearch.setSelectedIndex(index);
            }
        }
        this.trackObject.setSelected(track);
        this.idTextField.setText("" + objectId);
        actionPerformed(null);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(final ActionEvent e)
    {
        this.searchPanelEventProducer.fireEvent(new Event(ANIMATION_SEARCH_OBJECT_EVENT, getSourceId(),
                new Object[] {(ObjectKind<?>) this.typeToSearch.getSelectedItem(), this.idTextField.getText(),
                        this.trackObject.isSelected()}));
    }

    /** {@inheritDoc} */
    @Override
    public void focusGained(final FocusEvent e)
    {
        actionPerformed(null);
    }

    /** {@inheritDoc} */
    @Override
    public void focusLost(final FocusEvent e)
    {
        // Do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void insertUpdate(final DocumentEvent e)
    {
        actionPerformed(null);
    }

    /** {@inheritDoc} */
    @Override
    public void removeUpdate(final DocumentEvent e)
    {
        actionPerformed(null);
    }

    /** {@inheritDoc} */
    @Override
    public void changedUpdate(final DocumentEvent e)
    {
        actionPerformed(null);
    }

    /**
     * Entries in the typeToSearch JComboBox of the search panel.
     * @param <T> Type of object identified by key
     */
    public abstract static class ObjectKind<T extends Locatable>
    {
        /** The key of this ObjectKind. */
        private final String key;

        /**
         * Construct a new ObjectKind (entry in the combo box).
         * @param key String; the key of the new ObjectKind
         */
        public ObjectKind(final String key)
        {
            this.key = key;
        }

        /**
         * Retrieve the key.
         * @return String; the key
         */
        public String getKey()
        {
            return this.key;
        }

        /**
         * Lookup an object of type T in an network.
         * @param id String; id of the object to return
         * @return T; the object in the network of the correct type and matching id, or null if no matching object was found.
         */
        public abstract T searchObject(String id);

        /**
         * Produce the text that will appear in the combo box. This method should be overridden to implement localization.
         */
        @Override
        public String toString()
        {
            return this.key;
        }
    }

    /**
     * EventProducer to which to delegate the event producing methods.
     * <p>
     * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    class SearchPanelEventProducer extends EventProducer
    {
        /** */
        private static final long serialVersionUID = 20210213L;

        /** {@inheritDoc} */
        @Override
        public Serializable getSourceId()
        {
            return "SearchPanel.EventProducer";
        }

        /** {@inheritDoc} */
        @Override
        public void fireEvent(final EventInterface event)
        {
            super.fireEvent(event);
        }
    }

    /**
     * Return the delegate event producer.
     * @return SearchPanelEventProducer; the delegate event producer
     */
    public SearchPanelEventProducer getSearchPanelEventProducer()
    {
        return this.searchPanelEventProducer;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return this.searchPanelEventProducer.getSourceId();
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType) throws RemoteException
    {
        return this.searchPanelEventProducer.addListener(listener, eventType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType,
            final ReferenceType referenceType) throws RemoteException
    {
        return this.searchPanelEventProducer.addListener(listener, eventType, referenceType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType, final int position)
            throws RemoteException
    {
        return this.searchPanelEventProducer.addListener(listener, eventType, position);
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType, final int position,
            final ReferenceType referenceType) throws RemoteException
    {
        return this.searchPanelEventProducer.addListener(listener, eventType, position, referenceType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeListener(final EventListenerInterface listener, final EventTypeInterface eventType)
            throws RemoteException
    {
        return this.searchPanelEventProducer.removeListener(listener, eventType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasListeners() throws RemoteException
    {
        return this.searchPanelEventProducer.hasListeners();
    }

    /** {@inheritDoc} */
    @Override
    public int numberOfListeners(final EventTypeInterface eventType) throws RemoteException
    {
        return this.searchPanelEventProducer.numberOfListeners(eventType);
    }

    /** {@inheritDoc} */
    @Override
    public Set<EventTypeInterface> getEventTypesWithListeners() throws RemoteException
    {
        return this.searchPanelEventProducer.getEventTypesWithListeners();
    }

}
