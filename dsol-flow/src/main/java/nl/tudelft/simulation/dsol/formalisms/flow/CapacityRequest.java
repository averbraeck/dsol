package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.Objects;

/**
 * CapacityRequest contains a request for capacity of, e.g., a resource. CapacityRequest instances are typically stored in a
 * queue and they contain the queue entry time to implement reneging and calculate statistics.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public abstract class CapacityRequest<T extends Number & Comparable<T>>
{
    /** the numeric id of this request. */
    private final long id;

    /** the requestor of the amount of capacity. */
    private final CapacityRequestor<T> requestor;

    /** the amount requested by the requestor. */
    protected final Number amount;

    /** the priority of the request. */
    private final int priority;

    /** the time when the request was created. */
    private final T queueEntryTime;

    /**
     * Create a new capacity request.
     * @param id the numeric id of this request
     * @param requestor the requestor of the amount of capacity
     * @param amount the amount requested by the requestor
     * @param priority the priority of the request
     * @param queueEntryTime the time when the request was created
     */
    public CapacityRequest(final long id, final CapacityRequestor<T> requestor, final Number amount,
            final int priority, final T queueEntryTime)
    {
        this.id = id;
        this.requestor = requestor;
        this.amount = amount;
        this.priority = priority;
        this.queueEntryTime = queueEntryTime;
    }

    /**
     * Return the id number of the request.
     * @return the id number of the request
     */
    public long getId()
    {
        return this.id;
    }

    /**
     * Return the requestor of the capacity.
     * @return the requestor of the capacity
     */
    public CapacityRequestor<T> getRequestor()
    {
        return this.requestor;
    }

    /**
     * Return the requested amount of the resource.
     * @return the requested amount of the resource
     */
    public abstract Number getAmount();

    /**
     * Return the priority of the request (higher value is higher priority)
     * @return the priority of the request (higher value is higher priority)
     */
    public int getPriority()
    {
        return this.priority;
    }

    /**
     * Return the time when the request was created. This is typically the time when the request enters the queue.
     * @return the time when the request was created
     */
    public T getQueueEntryTime()
    {
        return this.queueEntryTime;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.id, this.priority, this.queueEntryTime);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CapacityRequest<?> other = (CapacityRequest<?>) obj;
        return this.id == other.id && this.priority == other.priority
                && Objects.equals(this.queueEntryTime, other.queueEntryTime);
    }

    /**
     * Capacity request for integer capacity.
     * @param <T> the time type
     */
    public static class IntegerCapacity<T extends Number & Comparable<T>> extends CapacityRequest<T>
    {
        /**
         * Create a new capacity request for integer capacity.
         * @param id the numeric id of this request
         * @param requestor the requestor of the amount of capacity
         * @param amount the amount requested by the requestor
         * @param priority the priority of the request
         * @param queueEntryTime the time when the request was created
         */
        public IntegerCapacity(final long id, final CapacityRequestor<T> requestor, final int amount, final int priority,
                final T queueEntryTime)
        {
            super(id, requestor, amount, priority, queueEntryTime);
        }

        @Override
        public Integer getAmount()
        {
            return this.amount.intValue();
        }

        @Override
        public String toString()
        {
            return "CapacityRequest.Integer [getId()=" + this.getId() + ", getAmount()=" + this.getAmount() + ", getPriority()="
                    + this.getPriority() + ", getQueueEntryTime()=" + this.getQueueEntryTime() + "]";
        }

    }

    /**
     * Capacity request for double capacity.
     * @param <T> the time type
     */
    public static class DoubleCapacity<T extends Number & Comparable<T>> extends CapacityRequest<T>
    {
        /**
         * Create a new capacity request for double capacity.
         * @param id the numeric id of this request
         * @param requestor the requestor of the amount of capacity
         * @param amount the amount requested by the requestor
         * @param priority the priority of the request
         * @param queueEntryTime the time when the request was created
         */
        public DoubleCapacity(final long id, final CapacityRequestor<T> requestor, final double amount, final int priority,
                final T queueEntryTime)
        {
            super(id, requestor, amount, priority, queueEntryTime);
        }

        @Override
        public Double getAmount()
        {
            return this.amount.doubleValue();
        }

        @Override
        public String toString()
        {
            return "CapacityRequest.Double [getId()=" + this.getId() + ", getAmount()=" + this.getAmount() + ", getPriority()="
                    + this.getPriority() + ", getQueueEntryTime()=" + this.getQueueEntryTime() + "]";
        }

    }

}
