/*
 * TestBean.java Created on May 3, 2001, 5:14 PM
 */

package nl.tudelft.dsol.introspection.beans;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

import org.pmw.tinylog.Logger;

/**
 * @author (c) 2003 <a href="http://www.tudelft.nl">Delft University of Technology </a>, Delft, the Netherlands <br>
 *         <a href="http://www.tbm.tudelft.nl">Faculty of Technology, Policy and Management </a> <br>
 *         <a href="http://www.sk.tbm.tudelft.nl">Department of System Engineering </a> <br>
 *         Main researcher : <a href="http://www.tbm.tudelft.nl/webstaf/alexandv/">Dr. Ir. A. Verbraeck</a><br>
 *         Assistant researchers <a href="https://www.linkedin.com/in/peterhmjacobs">Ir. P.H.M. Jacobs </a> and
 *         <a href="http://www.tbm.tudelft.nl/webstaf/nielsl">Ir. N.A. Lang </a>
 */
public class SubTestBean implements Serializable
{
    /** */
    private static final long serialVersionUID = 1L;

    /** Holds value of property firstProperty. */
    private String firstProperty = "First ;-)";

    /** Holds value of property secondProperty. */
    private String secondProperty = "Second ;-)";

    /** Holds value of property intProp. */
    private int intProp = 1717;

    /** Holds value of property color. */
    private Color color = Color.BLUE;

    /** Holds value of property font. */
    private Font font;

    /** Creates new TestBean. */
    public SubTestBean()
    {
        super();
    }

    /**
     * Getter for property firstProperty.
     * @return Value of property firstProperty.
     */
    public String getFirstProperty()
    {
        Logger.info(this.firstProperty + "requested.");
        return this.firstProperty;
    }

    /**
     * Setter for property firstProperty.
     * @param firstProperty New value of property firstProperty.
     */
    public void setFirstProperty(final String firstProperty)
    {
        Logger.info(this.firstProperty + "set.");
        this.firstProperty = firstProperty;
    }

    /**
     * Getter for property secondProperty.
     * @return Value of property secondProperty.
     */
    public String getSecondProperty()
    {
        Logger.info(this.secondProperty + "requested.");
        return this.secondProperty;
    }

    /**
     * Setter for property secondProperty.
     * @param secondProperty New value of property secondProperty.
     */
    public void setSecondProperty(final String secondProperty)
    {
        Logger.info(this.secondProperty + "set.");
        this.secondProperty = secondProperty;
    }

    /**
     * Getter for property intProp.
     * @return Value of property intProp.
     */
    public int getIntProp()
    {
        Logger.info(this.intProp + "requested.");
        return this.intProp;
    }

    /**
     * Setter for property intProp.
     * @param intProp New value of property intProp.
     */
    public void setIntProp(final int intProp)
    {
        Logger.info(this.intProp + "set.");
        this.intProp = intProp;
    }

    /**
     * Getter for property color.
     * @return Value of property color.
     */
    public Color getColor()
    {
        return this.color;
    }

    /**
     * Setter for property color.
     * @param color New value of property color.
     */
    public void setColor(final Color color)
    {
        this.color = color;
    }

    /**
     * Getter for property font.
     * @return Value of property font.
     */
    public Font getFont()
    {
        return this.font;
    }

    /**
     * Setter for property font.
     * @param font New value of property font.
     */
    public void setFont(final Font font)
    {
        this.font = font;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "" + this.getColor() + this.getFirstProperty() + this.getFont() + this.getIntProp() + this.getSecondProperty();
    }
}
