package nl.tudelft.simulation.dsol.demo.des.mm1.step06;

/**
 * Entity class for M/M/1 Discrete Event Simulation (DES) model example. See
 * <a href= "https://simulation.tudelft.nl/dsol/manual/1-getting-started/example-event/">DES Model Example</a> for more
 * information.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Entity
{
    /** time of creation for statistics. */
    private final double createTime;

    /** id number. */
    private final int id;

    /**
     * @param id int; entity id number
     * @param createTime double; time of creation for statistics
     */
    public Entity(final int id, final double createTime)
    {
        this.id = id;
        this.createTime = createTime;
    }

    /**
     * @return time of creation for statistics
     */
    public double getCreateTime()
    {
        return this.createTime;
    }

    /**
     * @return entity id number
     */
    public int getId()
    {
        return this.id;
    }

    @Override
    public String toString()
    {
        return "Entity [createTime=" + this.createTime + ", id=" + this.id + "]";
    }
}
