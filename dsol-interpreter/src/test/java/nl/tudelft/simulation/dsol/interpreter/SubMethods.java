package nl.tudelft.simulation.dsol.interpreter;

/**
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SubMethods
{
    /**
     * Subclass method.
     * @return number 10
     */
    protected int iSub10()
    {
        return 10;
    }

    /**
     * Subclass method.
     * @return number 10
     */
    protected int iOver5()
    {
        return 10;
    }

    /**
     * Subclass method.
     * @return number 3
     */
    protected int iPlus4()
    {
        return 3;
    }

    /**
     * Subclass method.
     * @param nul value = 0
     * @return number 1 + 2 + 3
     */
    protected int iPl123(int nul)
    {
        int i = nul;
        i += 1;
        i += 2;
        i += 3;
        return i;
    }

    /**
     * Subclass method.
     * @return String ABC
     */
    protected String sSubABC()
    {
        return "ABC";
    }

    /**
     * Subclass method.
     * @param abc value to add to the end
     * @return String DEF plus value in argument
     */
    protected String sPlusDEF(String abc)
    {
        return "DEF" + abc;
    }

}
