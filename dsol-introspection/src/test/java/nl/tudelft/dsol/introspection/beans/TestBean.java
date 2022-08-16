package nl.tudelft.dsol.introspection.beans;

import java.awt.Font;

import org.pmw.tinylog.Logger;

/**
 * @author (c) 2003 <a href="http://www.tudelft.nl">Delft University of Technology </a>, Delft, the Netherlands <br>
 *         <a href="http://www.tbm.tudelft.nl">Faculty of Technology, Policy and Management </a> <br>
 *         <a href="http://www.sk.tbm.tudelft.nl">Department of System Engineering </a> <br>
 *         Main researcher : <a href="http://www.tbm.tudelft.nl/webstaf/alexandv/">Dr. Ir. A. Verbraeck</a><br>
 *         Assistant researchers <a href="https://www.linkedin.com/in/peterhmjacobs">Ir. P.H.M. Jacobs </a> and
 *         <a href="http://www.tbm.tudelft.nl/webstaf/nielsl">Ir. N.A. Lang </a>
 */
public class TestBean extends java.lang.Object implements java.io.Serializable
{
    /** Holds value of property firstProperty. */
    private String firstProperty = "First ;-)";

    /** Holds value of property secondProperty. */
    private String secondProperty = "Second ;-)";

    /** Holds value of property intProp. */
    private int intProp = 1717;

    /** Holds value of property color. */
    private java.awt.Color color = java.awt.Color.yellow;

    /** Holds value of property font. */
    private Font font = null;

    /** Holds value of property subBean. */
    private SubTestBean subBean = null;

    /** Holds value of property testBean2. */
    private SubTestBean2Interface testBean2 = new SubTestBean2();

    /** Creates new TestBean. */
    public TestBean()
    {
        super();
    }

    /**
     * Getter for property firstProperty.
     * @return Value of property firstProperty.
     */
    public String getFirstProperty()
    {
        // Logger.info(this + this.firstProperty + "requested.");
        return this.firstProperty;
    }

    /**
     * Setter for property firstProperty.
     * @param firstProperty New value of property firstProperty.
     */
    public void setFirstProperty(String firstProperty)
    {
        Logger.info(this + firstProperty + "set.");
        this.firstProperty = firstProperty;
    }

    /**
     * Getter for property secondProperty.
     * @return Value of property secondProperty.
     */
    public String getSecondProperty()
    {
        // Logger.info(this + this.secondProperty + "requested.");
        return this.secondProperty;
    }

    /**
     * Setter for property secondProperty.
     * @param secondProperty New value of property secondProperty.
     */
    public void setSecondProperty(String secondProperty)
    {
        Logger.info(this + secondProperty + "set.");
        this.secondProperty = secondProperty;
    }

    /**
     * Getter for property intProp.
     * @return Value of property intProp.
     */
    public int getIntProp()
    {
        // Logger.info(this +"" + this.intProp + "requested");
        return this.intProp;
    }

    /**
     * Setter for property intProp.
     * @param intProp New value of property intProp.
     */
    public void setIntProp(int intProp)
    {
        Logger.info(this + "intProp set to " + intProp);
        this.intProp = intProp;
    }

    /**
     * Getter for property color.
     * @return Value of property color.
     */
    public java.awt.Color getColor()
    {
        return this.color;
    }

    /**
     * Setter for property color.
     * @param color New value of property color.
     */
    public void setColor(java.awt.Color color)
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
    public void setFont(Font font)
    {
        this.font = font;
    }

    /**
     * Getter for property subBean.
     * @return Value of property subBean.
     */
    public SubTestBean getSubBean()
    {
        return this.subBean;
    }

    /**
     * Setter for property subBean.
     * @param subBean New value of property subBean.
     */
    public void setSubBean(SubTestBean subBean)
    {
        this.subBean = subBean;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        String result;
        result = this.getColor() + this.getFirstProperty() + this.getSecondProperty() + this.getFont() + this.getIntProp();
        return result;
    }

    /**
     * Getter for property testBean2.
     * @return Value of property testBean2.
     */
    public SubTestBean2Interface getTestBean2()
    {
        return this.testBean2;
    }

    /**
     * Setter for property testBean2.
     * @param testBean2 New value of property testBean2.
     */
    public void setTestBean2(SubTestBean2Interface testBean2)
    {
        this.testBean2 = testBean2;
    }
}
