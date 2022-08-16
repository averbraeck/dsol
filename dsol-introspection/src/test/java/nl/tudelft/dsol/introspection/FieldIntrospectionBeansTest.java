package nl.tudelft.dsol.introspection;

import org.pmw.tinylog.Logger;

import nl.tudelft.dsol.introspection.beans.SubTestBean2;
import nl.tudelft.simulation.introspection.Property;
import nl.tudelft.simulation.introspection.fields.FieldIntrospector;

/**
 * A test program for the field introspection implementation.
 * @author (c) 2003 <a href="http://www.tudelft.nl">Delft University of Technology </a>, Delft, the Netherlands <br>
 *         <a href="http://www.tbm.tudelft.nl">Faculty of Technology, Policy and Management </a> <br>
 *         <a href="http://www.sk.tbm.tudelft.nl">Department of System Engineering </a> <br>
 *         Main researcher : <a href="http://www.tbm.tudelft.nl/webstaf/alexandv/">Dr. Ir. A. Verbraeck</a><br>
 *         Assistant researchers <a href="https://www.linkedin.com/in/peterhmjacobs">Ir. P.H.M. Jacobs </a> and
 *         <a href="http://www.tbm.tudelft.nl/webstaf/nielsl">Ir. N.A. Lang </a>
 */
public final class FieldIntrospectionBeansTest
{
    /**
     * constructs a new PTestFieldIntrospector.
     */
    private FieldIntrospectionBeansTest()
    {
        super();
        // unreachable code
    }

    /**
     * executes the PTestFieldIntrospector.
     * @param args the command line arguments
     */
    public static void main(final String[] args)
    {
        try
        {
            Logger.info("Running field introspector test");
            Property[] props = (new FieldIntrospector()).getProperties(new SubTestBean2());
            for (int i = 0; i < props.length; i++)
            {
                Logger.info("Prop name: {}", props[i].getName());
                Logger.info("Prop class: {}", props[i].getType());
                Logger.info("Prop value: {}", props[i].getValue());
                Logger.info("Setting Possible? ");
                props[i].setValue("TEST");
                Logger.info("If so, 'TEST' should be retrieved: {}", props[i].getValue());
            }
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
}
