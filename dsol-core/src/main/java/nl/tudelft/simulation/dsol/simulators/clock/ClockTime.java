package nl.tudelft.simulation.dsol.simulators.clock;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.djunits.unit.TimeUnit;
import org.djunits.value.vdouble.scalar.Time;

/**
 * ClockTime is an extension of DJUNITS time, aware of the calendar and clock time. It can read and display ISO time. The
 * ClockTime is used as the absolute time unit in the ClockDevsSimulator and ClockDevsAnimator.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class ClockTime extends Time
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Create a clock time as an extension of a djunits Time.
     * @param value the Time to store in this ClockTime.
     */
    public ClockTime(final Time value)
    {
        super(value);
    }

    /**
     * Create a clock time as an extension of a djunits Time.
     * @param value the double value of the Time object
     * @param unit the djunits Time unit to use
     */
    public ClockTime(final double value, final TimeUnit unit)
    {
        super(value, unit);
    }

    /**
     * Return the ClockTime object as a LocalDateTime.
     * @return the ClockTime object as a LocalDateTime
     */
    public LocalDateTime localDateTime()
    {
        return LocalDateTime.ofEpochSecond(Math.round(getSI()), 0, ZoneOffset.UTC);
    }

    /**
     * Format the ClockTime as a yyyy-MM-dd local date.
     * @return a string representing the ClockTime as a yyyy-MM-dd local date
     */
    public String ymd()
    {
        return localDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Format the ClockTime as a hh:mm:ss local time using a 24-hour clock.
     * @return a string representing the ClockTime as a hh:mm:ss local time
     */
    public String hms()
    {
        return localDateTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    /**
     * Format the ClockTime as a hh:mm local time using a 24-hour clock.
     * @return a string representing the ClockTime as a hh:mm local time
     */
    public String hm()
    {
        return hms().substring(0, 5);
    }

    /**
     * Format the ClockTime as a yyyy-MM-dd hh:mm local date and time using a 24-hour clock.
     * @return a string representing the ClockTime as a yyyy-MM-dd hh:mm local date and time
     */
    public String ymdhm()
    {
        return ymd() + " " + hm();
    }

    /**
     * Return the day of the week of the ClockTime.
     * @return the day of the week of the ClockTime
     */
    public DayOfWeek dayOfWeek()
    {
        return localDateTime().getDayOfWeek();
    }

    /**
     * Return the int for the day of the week of the ClockTime with 1=MONDAY, 7=SUNDAY.
     * @return the int for the day of the week of the ClockTime with 1=MONDAY, 7=SUNDAY
     */
    public int dayOfWeekInt()
    {
        return dayOfWeek().getValue();
    }

    /**
     * Instantiate a ClockTime based on an ISO date and time (yyyy-MM-ddThh:mm:ss) with a precision of one second.
     * @param isoDateTime a String of an ISO date and time (yyyy-MM-ddThh:mm:ss)
     * @return a ClockTime for the ISO date and time with a precision of one second
     */
    public static ClockTime ofIso(final String isoDateTime)
    {
        double seconds = LocalDateTime.parse(isoDateTime).toEpochSecond(ZoneOffset.UTC);
        return new ClockTime(seconds, TimeUnit.EPOCH_SECOND);
    }

    @Override
    public String toString()
    {
        return ymdhm();
    }

}
