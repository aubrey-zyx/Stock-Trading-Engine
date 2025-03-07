package com.tradingengine.orderbook;

import com.tradingengine.models.Order;
import com.tradingengine.utils.PriorityQueueWithoutLock;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Order book for a certain ticker. Stores BUY and SELL orders in separate FIFO queues.
 * BUY orders are sorted by price in descending order.
 * SELL order are sorted by price in ascending order.
 * For orders at the same price, earlier orders will be matched first.
 */
public class OrderBook {
    private final PriorityQueueWithoutLock buyOrders = new PriorityQueueWithoutLock();
    private final PriorityQueueWithoutLock sellOrders = new PriorityQueueWithoutLock();

    // Prevent multiple threads from processing simultaneously
    private final AtomicBoolean matchingInProgress = new AtomicBoolean(false);

    public void addOrder(Order order) {
        if (order.getType() == Order.Type.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
    }

    public PriorityQueueWithoutLock getBuyOrders() { return buyOrders; }
    public PriorityQueueWithoutLock getSellOrders() { return sellOrders; }

    // Thread-safe method to ensure only one thread matches orders at a time
    public boolean startMatching() {
        return matchingInProgress.compareAndSet(false, true);
    }

    public void finishMatching() {
        matchingInProgress.set(false);
    }
}
