/*
 * SubTestBean2.java Created on May 31, 2001, 11:01 AM
 */

package nl.tudelft.dsol.introspection.beans;

/**
 * @author (c) 2003 <a href="http://www.tudelft.nl">Delft University of Technology </a>, Delft, the Netherlands <br>
 *         <a href="http://www.tbm.tudelft.nl">Faculty of Technology, Policy and Management </a> <br>
 *         <a href="http://www.sk.tbm.tudelft.nl">Department of System Engineering </a> <br>
 *         Main researcher : <a href="http://www.tbm.tudelft.nl/webstaf/alexandv/">Dr. Ir. A. Verbraeck</a><br>
 *         Assistant researchers <a href="https://www.linkedin.com/in/peterhmjacobs">Ir. P.H.M. Jacobs </a> and
 *         <a href="http://www.tbm.tudelft.nl/webstaf/nielsl">Ir. N.A. Lang </a>
 */
public class SubTestBean2 extends Object implements SubTestBean2Interface
{
    /** property 1 */
    private String prop1 = "prop1_value";

    /** Holds value of property testProp2. */
    private String testProp2;

    /** Creates new SubTestBean2 */
    public SubTestBean2()
    {
        super();
    }

    /**
     * Getter for property testProp1.
     * @return Value of property testProp1.
     */
    public String getTestProp1()
    {
        return this.prop1;
    }

    /**
     * Setter for property testProp1.
     * @param testProp1 New value of property testProp1.
     */
    public void setTestProp1(final String testProp1)
    {
        this.prop1 = testProp1;
    }

    /**
     * Getter for property testProp2.
     * @return Value of property testProp2.
     */
    public String getTestProp2()
    {
        return this.testProp2;
    }

    /**
     * Setter for property testProp2.
     * @param testProp2 New value of property testProp2.
     */
    public void setTestProp2(final String testProp2)
    {
        this.testProp2 = testProp2;
    }

}
