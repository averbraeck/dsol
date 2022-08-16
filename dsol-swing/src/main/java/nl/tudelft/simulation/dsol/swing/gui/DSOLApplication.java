package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nl.tudelft.simulation.dsol.swing.animation.D2.AnimationPanel;
import nl.tudelft.simulation.dsol.swing.gui.appearance.Appearance;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControl;

/**
 * The DSOLSimulationApplication allows to execute and control a simulation model, add tabs, and provide insight into the state
 * of the simulation. It does not have animation -- the DSOLAnimationApplication adds the DSOLAnimationPAnel for that specific
 * purpose.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DSOLApplication extends JFrame
{
    /** */
    private static final long serialVersionUID = 20190118L;

    /** the content pane of this application. */
    private final DSOLPanel panel;

    /** whether the application has been closed or not. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean closed = false;

    /** Properties for the frame appearance (not simulation related). */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Properties frameProperties;

    /** Current appearance. */
    private Appearance appearance = Appearance.BRIGHT;

    /**
     * @param panel JPanel; the simulation panel (or console or animation panel) to be used on the main page
     * @param title String; the window title
     */
    public DSOLApplication(final DSOLPanel panel, final String title)
    {
        this.panel = panel;
        setTitle(title);
        setContentPane(panel);
        pack();
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setVisible(true);

        setExitOnClose(true);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(final WindowEvent windowEvent)
            {
                DSOLApplication.this.closed = true;
                super.windowClosing(windowEvent);
            }
        });

        //////////////////////
        ///// Appearance /////
        //////////////////////

        // Listener to write frame properties on frame close
        String sep = System.getProperty("file.separator");
        String propertiesFile = System.getProperty("user.home") + sep + "dsol" + sep + "properties.ini";
        addWindowListener(new WindowAdapter()
        {
            /** {@inheritDoce} */
            @Override
            public void windowClosing(final WindowEvent windowEvent)
            {
                try
                {
                    File f = new File(propertiesFile);
                    f.getParentFile().mkdirs();
                    FileWriter writer = new FileWriter(f);
                    DSOLApplication.this.frameProperties.store(writer, "DSOL user settings");
                }
                catch (IOException exception)
                {
                    System.err.println("Could not store properties at " + propertiesFile + ".");
                }
            }
        });

        // Set default frame properties and load properties from file (if any)
        Properties defaults = new Properties();
        defaults.setProperty("Appearance", "BRIGHT");
        this.frameProperties = new Properties(defaults);
        try
        {
            FileReader reader = new FileReader(propertiesFile);
            this.frameProperties.load(reader);
        }
        catch (IOException ioe)
        {
            // ok, use defaults
        }
        this.appearance = Appearance.valueOf(this.frameProperties.getProperty("Appearance").toUpperCase());

        /** Menu class to only accept the font of an Appearance */
        class AppearanceControlMenu extends JMenu implements AppearanceControl
        {
            /** */
            private static final long serialVersionUID = 20180206L;

            /**
             * Constructor.
             * @param string String; string
             */
            AppearanceControlMenu(final String string)
            {
                super(string);
            }

            /** {@inheritDoc} */
            @Override
            public boolean isFont()
            {
                return true;
            }

            /** {@inheritDoc} */
            @Override
            public String toString()
            {
                return "AppearanceControlMenu []";
            }
        }

        // Appearance menu
        JMenu app = new AppearanceControlMenu("Appearance");
        app.addMouseListener(new SubMenuShower(app));
        ButtonGroup appGroup = new ButtonGroup();
        for (Appearance appearanceValue : Appearance.values())
        {
            appGroup.add(addAppearance(app, appearanceValue));
        }

        /** PopupMenu class to only accept the font of an Appearance */
        class AppearanceControlPopupMenu extends JPopupMenu implements AppearanceControl
        {
            /** */
            private static final long serialVersionUID = 20180206L;

            /** {@inheritDoc} */
            @Override
            public boolean isFont()
            {
                return true;
            }

            /** {@inheritDoc} */
            @Override
            public String toString()
            {
                return "AppearanceControlPopupMenu []";
            }
        }

        // Popup menu to change appearance
        JPopupMenu popMenu = new AppearanceControlPopupMenu();
        popMenu.add(app);
        panel.setComponentPopupMenu(popMenu);

        // Set the Appearance as by frame properties
        setAppearance(getAppearance()); // color elements that were just added
    }

    /**
     * Sets an appearance.
     * @param appearance Appearance; appearance
     */
    public void setAppearance(final Appearance appearance)
    {
        this.appearance = appearance;
        setAppearance(this.getContentPane(), appearance);
        this.frameProperties.setProperty("Appearance", appearance.toString());
    }

    /**
     * Sets an appearance recursively on components.
     * @param c Component; visual component
     * @param appear Appearance; look and feel
     */
    private void setAppearance(final Component c, final Appearance appear)
    {
        if (c instanceof AppearanceControl)
        {
            AppearanceControl ac = (AppearanceControl) c;
            if (ac.isBackground())
            {
                c.setBackground(appear.getBackground());
            }
            if (ac.isForeground())
            {
                c.setForeground(appear.getForeground());
            }
            if (ac.isFont())
            {
                changeFont(c, appear.getFont());
            }
        }
        else if (c instanceof AnimationPanel)
        {
            // animation backdrop
            c.setBackground(appear.getBackdrop()); // not background
            c.setForeground(appear.getForeground());
            changeFont(c, appear.getFont());
        }
        else
        {
            // default
            c.setBackground(appear.getBackground());
            c.setForeground(appear.getForeground());
            changeFont(c, appear.getFont());
        }
        if (c instanceof JSlider)
        {
            // labels of the slider
            Dictionary<?, ?> dictionary = ((JSlider) c).getLabelTable();
            Enumeration<?> keys = dictionary.keys();
            while (keys.hasMoreElements())
            {
                JLabel label = (JLabel) dictionary.get(keys.nextElement());
                label.setForeground(appear.getForeground());
                label.setBackground(appear.getBackground());
            }
        }
        // children
        if (c instanceof JComponent)
        {
            for (Component child : ((JComponent) c).getComponents())
            {
                setAppearance(child, appear);
            }
        }
    }

    /**
     * Change font on component.
     * @param c Component; component
     * @param font String; font name
     */
    private void changeFont(final Component c, final String font)
    {
        Font prev = c.getFont();
        c.setFont(new Font(font, prev.getStyle(), prev.getSize()));
    }

    /**
     * Returns the appearance.
     * @return Appearance; appearance
     */
    public Appearance getAppearance()
    {
        return this.appearance;
    }

    /**
     * Adds an appearance to the menu.
     * @param group JMenu; menu to add item to
     * @param appear Appearance; appearance this item selects
     * @return JMenuItem; menu item
     */
    private JMenuItem addAppearance(final JMenu group, final Appearance appear)
    {
        JCheckBoxMenuItem check = new StayOpenCheckBoxMenuItem(appear.getName(), appear.equals(getAppearance()));
        check.addMouseListener(new MouseAdapter()
        {
            /** {@inheritDoc} */
            @Override
            public void mouseClicked(final MouseEvent e)
            {
                setAppearance(appear);
            }
        });
        return group.add(check);
    }

    /**
     * Set the behavior on closing the window.
     * @param exitOnClose boolean; set exitOnClose
     */
    public void setExitOnClose(final boolean exitOnClose)
    {
        if (exitOnClose)
        {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
        else
        {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }
    }

    /**
     * Return the panel with the controls and the tabbed content pane.
     * @return DSOLPanel; the panel with the controls and the tabbed content pane
     */
    public DSOLPanel getDSOLPanel()
    {
        return this.panel;
    }

    /**
     * Return whether the window has been closed.
     * @return boolean; whether the window has been closed
     */
    public boolean isClosed()
    {
        return this.closed;
    }

    /**
     * Mouse listener which shows the submenu when the mouse enters the button.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
     * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
     */
    private class SubMenuShower extends MouseAdapter
    {
        /** The menu. */
        private JMenu menu;

        /**
         * Constructor.
         * @param menu JMenu; menu
         */
        SubMenuShower(final JMenu menu)
        {
            this.menu = menu;
        }

        /** {@inheritDoc} */
        @Override
        public void mouseEntered(final MouseEvent e)
        {
            MenuSelectionManager.defaultManager().setSelectedPath(
                    new MenuElement[] {(MenuElement) this.menu.getParent(), this.menu, this.menu.getPopupMenu()});
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "SubMenuShower [menu=" + this.menu + "]";
        }
    }

    /**
     * Check box item that keeps the popup menu visible after clicking, so the user can click and try some options.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
     * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
     */
    private static class StayOpenCheckBoxMenuItem extends JCheckBoxMenuItem implements AppearanceControl
    {
        /** */
        private static final long serialVersionUID = 20180206L;

        /** Stored selection path. */
        private static MenuElement[] path;

        {
            getModel().addChangeListener(new ChangeListener()
            {

                @Override
                public void stateChanged(final ChangeEvent e)
                {
                    if (getModel().isArmed() && isShowing())
                    {
                        setPath(MenuSelectionManager.defaultManager().getSelectedPath());
                    }
                }
            });
        }

        /**
         * Sets the path.
         * @param path MenuElement[]; path
         */
        public static void setPath(final MenuElement[] path)
        {
            StayOpenCheckBoxMenuItem.path = path;
        }

        /**
         * Constructor.
         * @param text String; menu item text
         * @param selected boolean; if the item is selected
         */
        StayOpenCheckBoxMenuItem(final String text, final boolean selected)
        {
            super(text, selected);
        }

        /** {@inheritDoc} */
        @Override
        public void doClick(final int pressTime)
        {
            super.doClick(pressTime);
            for (MenuElement element : path)
            {
                if (element instanceof JComponent)
                {
                    ((JComponent) element).setVisible(true);
                }
            }
            JMenu menu = (JMenu) path[path.length - 3];
            MenuSelectionManager.defaultManager()
                    .setSelectedPath(new MenuElement[] {(MenuElement) menu.getParent(), menu, menu.getPopupMenu()});
        }

        /** {@inheritDoc} */
        @Override
        public boolean isFont()
        {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "StayOpenCheckBoxMenuItem []";
        }
    }
}
