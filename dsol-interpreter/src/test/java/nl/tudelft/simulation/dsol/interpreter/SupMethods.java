package nl.tudelft.simulation.dsol.interpreter;

/**
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SupMethods extends SubMethods
{
    /**
     * Subclass method.
     * @return number 10
     */
    @Override
    protected int iSub10()
    {
        return super.iSub10();
    }

    /**
     * Subclass method.
     * @return number 10
     */
    @Override
    protected int iOver5()
    {
        return 5;
    }

    /**
     * Subclass method.
     * @return number 4 plus super.iPlus4()
     */
    @Override
    protected int iPlus4()
    {
        return 4 + super.iPlus4();
    }

    /**
     * Subclass method.
     * @return number 0 plus super.iPl123()
     */
    @Override
    protected int iPl123(int nul)
    {
        return super.iPl123(nul);
    }

    /**
     * Subclass method.
     * @return String "ABC"
     */
    @Override
    protected String sSubABC()
    {
        return super.sSubABC();
    }

    /**
     * Subclass method.
     * @return String "ABC" plus value of super.sPlusDEF
     */
    @Override
    protected String sPlusDEF(String abc)
    {
        return abc + super.sPlusDEF(abc);
    }

}
