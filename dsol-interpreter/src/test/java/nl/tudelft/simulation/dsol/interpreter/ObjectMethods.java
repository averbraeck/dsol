package nl.tudelft.simulation.dsol.interpreter;

/**
 * A number of long methods to test the interpreted bytecode for longs.
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ObjectMethods
{
    /** value to use in the test. */
    private Object value;

    /**
     * @param value value to use in the test
     */
    public ObjectMethods(final Object value)
    {
        super();
        this.value = value;
    }

    /**
     * @return value
     */
    public Object get()
    {
        return this.value;
    }

    /**
     * @param value set value
     */
    public void set(final Object value)
    {
        this.value = value;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ObjectMethods other = (ObjectMethods) obj;
        if (this.value == null)
        {
            if (other.value != null)
                return false;
        }
        else if (!this.value.equals(other.value))
            return false;
        return true;
    }

    /**
     * @param obj object to compare with
     * @return hashcode equal or not
     */
    public boolean equalsHC(final Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }

    /**
     * Concat 5 objects.
     * @param s1 String; string nr 1
     * @param s2 String; string nr 2
     * @param s3 String; string nr 3
     * @param s4 String; string nr 4
     * @param s5 String; string nr 5
     * @return concatenation of the toString of the objects.
     */
    public static String concat5(final Object s1, final Object s2, final Object s3, final Object s4, final Object s5)
    {
        return s1.toString() + s2.toString() + s3.toString() + s4.toString() + s5.toString();
    }

    /**
     * Concat N objects.
     * @param objects the string objects to concat
     * @return the concatenation of the toString of the objects.
     */
    public static String concatN(final Object... objects)
    {
        String ret = "";
        for (Object s : objects)
        {
            ret += s.toString();
        }
        return ret;
    }

    /**
     * @return class info from toString().
     */
    public String getC()
    {
        String ret = "";
        Class<?> c = getClass();
        String n = c.getName();
        ret += n;
        ret += "@";
        int hc = hashCode();
        String hcs = Integer.toHexString(hc);
        ret += hcs;
        return ret;
        // return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
}
