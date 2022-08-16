package nl.tudelft.simulation.dsol;

/**
 * Interface for an identifiable class, which can return an id. Preferably the id is unique in a certain context.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the type of Id that is returned by an Identifiable, e.g. String
 */
public interface Identifiable<T>
{
    /**
     * @return the Id, which is preferably unique in a certain context
     */
    T getId();

    /**
     * String interface for an identifiable class. <br>
     * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
     * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>.
     * <br>
     * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public interface String extends Identifiable<String>
    {
        @Override
        String getId();
    }

    /**
     * Long interface for an identifiable class. <br>
     * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
     * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>.
     * <br>
     * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public interface Long extends Identifiable<Long>
    {
        @Override
        Long getId();
    }

}
