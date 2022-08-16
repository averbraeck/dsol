package nl.tudelft.simulation.dsol.simtime;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;

import nl.tudelft.simulation.language.DSOLRuntimeException;

/**
 * SimTime contains a number of static methods to deal with adding and ubstracting simulation times.
 * <p>
 * Copyright (c) 2021-2021 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class SimTime
{
    /** hash for Double. */
    private static final int hashDouble = Double.class.hashCode();

    /** hash for Float. */
    private static final int hashFloat = Float.class.hashCode();

    /** hash for Long. */
    private static final int hashLong = Long.class.hashCode();

    /** hash for Duration. */
    private static final int hashDuration = Duration.class.hashCode();

    /** hash for FloatDuration. */
    private static final int hashFloatDuration = FloatDuration.class.hashCode();

    /** dummy constructor. */
    private SimTime()
    {
        // dummy constructor
    }

    /**
     * Add two numbers.
     * @param a Number; first number
     * @param b Number; second number
     * @return Number; the sum of the two numbers
     * @param <T> The time type, e.g., Double, Long or Duration
     */
    @SuppressWarnings({"unchecked", "checkstyle:needbraces"})
    public static <T extends Number & Comparable<T>> T plus(final T a, final T b)
    {
        int hash = a.getClass().hashCode();
        if (hash == hashDouble)
            return (T) Double.valueOf(a.doubleValue() + b.doubleValue());
        if (hash == hashFloat)
            return (T) Float.valueOf(a.floatValue() + b.floatValue());
        if (hash == hashLong)
            return (T) Long.valueOf(a.longValue() + b.longValue());
        if (hash == hashDuration)
            return (T) ((Duration) a).plus((Duration) b);
        if (hash == hashFloatDuration)
            return (T) ((FloatDuration) a).plus((FloatDuration) b);

        throw new DSOLRuntimeException("SimTime.plus called for unknown time class: " + a.getClass().getSimpleName());
    }

    /**
     * Subtract two numbers.
     * @param a Number; first number
     * @param b Number; second number
     * @return Number; the difference of the two numbers
     * @param <T> The time type, e.g., Double, Long or Duration
     */
    @SuppressWarnings({"unchecked", "checkstyle:needbraces"})
    public static <T extends Number & Comparable<T>> T minus(final T a, final T b)
    {
        int hash = a.getClass().hashCode();
        if (hash == hashDouble)
            return (T) Double.valueOf(a.doubleValue() - b.doubleValue());
        if (hash == hashFloat)
            return (T) Float.valueOf(a.floatValue() - b.floatValue());
        if (hash == hashLong)
            return (T) Long.valueOf(a.longValue() - b.longValue());
        if (hash == hashDuration)
            return (T) ((Duration) a).minus((Duration) b);
        if (hash == hashFloatDuration)
            return (T) ((FloatDuration) a).minus((FloatDuration) b);

        throw new DSOLRuntimeException("SimTime.minus called for unknown time class: " + a.getClass().getSimpleName());
    }

    /**
     * Return a copy (clone) of the number.
     * @param a Number; the number to copy
     * @return Number; a copy of the number
     * @param <T> The time type, e.g., Double, Long or Duration
     */
    @SuppressWarnings({"unchecked", "checkstyle:needbraces"})
    public static <T extends Number & Comparable<T>> T copy(final T a)
    {
        int hash = a.getClass().hashCode();
        if (hash == hashDouble)
            return (T) Double.valueOf(a.doubleValue());
        if (hash == hashFloat)
            return (T) Float.valueOf(a.floatValue());
        if (hash == hashLong)
            return (T) Long.valueOf(a.longValue());
        if (hash == hashDuration)
            return (T) new Duration((Duration) a);
        if (hash == hashFloatDuration)
            return (T) new FloatDuration((FloatDuration) a);

        throw new DSOLRuntimeException("SimTime.copy called for unknown time class: " + a.getClass().getSimpleName());
    }

    /**
     * Return a zero value for the number.
     * @param a Number; the number to copy
     * @return Number; a zero value for the number
     * @param <T> The time type, e.g., Double, Long or Duration
     */
    @SuppressWarnings({"unchecked", "checkstyle:needbraces"})
    public static <T extends Number & Comparable<T>> T zero(final T a)
    {
        int hash = a.getClass().hashCode();
        if (hash == hashDouble)
            return (T) Double.valueOf(0.0d);
        if (hash == hashFloat)
            return (T) Float.valueOf(0.0f);
        if (hash == hashLong)
            return (T) Long.valueOf(0L);
        if (hash == hashDuration)
            return (T) Duration.ZERO;
        if (hash == hashFloatDuration)
            return (T) FloatDuration.ZERO;

        throw new DSOLRuntimeException("SimTime.copy called for unknown time class: " + a.getClass().getSimpleName());
    }
}
