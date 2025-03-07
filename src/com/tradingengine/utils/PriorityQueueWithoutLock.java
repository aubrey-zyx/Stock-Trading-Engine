package com.tradingengine.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import com.tradingengine.models.Order;

/**
 * Lock-free priority queue that sorts by (price, timestamp).
 */
public class PriorityQueueWithoutLock {
    private final ConcurrentLinkedQueue<Order> queue = new ConcurrentLinkedQueue<>();

    public void add(Order order) {
        ConcurrentLinkedQueue<Order> newQueue = new ConcurrentLinkedQueue<>();
        boolean added = false;

        for (Order existing : queue) {
            if (!added && order.compareTo(existing) < 0) {
                newQueue.add(order);
                added = true;
            }
            newQueue.add(existing);
        }

        if (!added) newQueue.add(order);
        queue.clear();
        queue.addAll(newQueue);
    }

    public Order peek() {
        return queue.peek();
    }

    public Order poll() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
