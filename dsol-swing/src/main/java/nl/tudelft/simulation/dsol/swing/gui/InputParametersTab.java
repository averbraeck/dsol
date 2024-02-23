package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameter;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;

/**
 * InputParametersTab displays the input parameters in a ScrollPane.
 * <p>
 * Copyright (c) 2021-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InputParametersTab extends JPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The parameter map. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public InputParameterMap inputParameterMap;
    
    /**
     * Create an InputParametersTab for the given simulation model.
     * @param model DsolModel; the model.
     */
    public InputParametersTab(final DsolModel<?, ?> model)
    {
        this.inputParameterMap = model.getInputParameterMap();
        JEditorPane textPane = new JEditorPane("text/html", "");
        textPane.setOpaque(true);
        textPane.setBackground(Color.WHITE);
        textPane.setEditable(true);
        textPane.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        JScrollPane scrollPane = new JScrollPane(textPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("<html>\n");
        textBuilder.append(makeHead());
        textBuilder.append(makeBody());
        textBuilder.append("</html>\n");
        textPane.setText(textBuilder.toString());
    }
        
    /**
     * Return the head portion of the HTML page to display the parameter map.
     * @return String; the head portion of the HTML page to display the parameter map
     */
    protected String makeHead()
    {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("  <head>\n");
        textBuilder.append("    <style>\n");
        textBuilder.append("      body {\n");
        textBuilder.append("        font-family: verdana;\n        font-size: 14pt;\n");
        textBuilder.append("      }\n");
        textBuilder.append("    </style>\n");
        textBuilder.append("  </head>\n");
        return textBuilder.toString();
    }
    
    /**
     * Return the body portion of the HTML page to display the parameter map.
     * @return String; the body portion of the HTML page to display the parameter map
     */
    protected String makeBody()
    {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("  <body style=\"background-color:white;\">\n  <h1>Parameters</h1><br>\n");
        for (InputParameter<?, ?> tab : this.inputParameterMap.getSortedSet())
        {
            textBuilder.append("\n  <br>\n  <h2>" + tab.getDescription() + "</h2>\n");
            InputParameterMap tabbedMap = (InputParameterMap) tab;
            for (InputParameter<?, ?> parameter : tabbedMap.getSortedSet())
            {
                textBuilder.append("  " + parameter.toString() + "<br>\n");
            }
        }
        textBuilder.append("  </body>\n");
        return textBuilder.toString();
    }
}
