package com.tradingengine.engine;

import com.tradingengine.models.Order;
import com.tradingengine.orderbook.OrderBook;
import com.tradingengine.utils.PriorityQueueWithoutLock;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages stock trading logic.
 */
public class TradingEngine {
    private final ConcurrentLinkedQueue<OrderBook> orderBooks = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> tickers = new ConcurrentLinkedQueue<>();

    public void addOrder(Order.Type type, String ticker, int quantity, double price) {
        Order order = new Order(type, ticker, quantity, price);
        OrderBook orderBook = getOrderBook(ticker);
        orderBook.addOrder(order);
        matchOrders(orderBook);
    }

    private OrderBook getOrderBook(String ticker) {
        int index = -1;

        // Find existing order book for this ticker
        int i = 0;
        for (String t : tickers) {
            if (t.equals(ticker)) {
                index = i;
                break;
            }
            i++;
        }

        // If found, return the existing order book
        if (index != -1) {
            int j = 0;
            for (OrderBook book : orderBooks) {
                if (j == index) return book;
                j++;
            }
        }

        // If not found, create a new order book
        OrderBook newBook = new OrderBook();
        tickers.add(ticker);
        orderBooks.add(newBook);
        return newBook;
    }

    private void matchOrders(OrderBook orderBook) {
        // Prevent multiple threads from matching the same order book simultaneously
        if (!orderBook.startMatching()) return;

        PriorityQueueWithoutLock buys = orderBook.getBuyOrders();
        PriorityQueueWithoutLock sells = orderBook.getSellOrders();

        while (!buys.isEmpty() && !sells.isEmpty()) {
            Order buy = buys.peek();
            Order sell = sells.peek();

            if (buy == null || sell == null) break;

            if (buy.getPrice() >= sell.getPrice()) {
                int matchedQty = Math.min(buy.getQuantity(), sell.getQuantity());

                System.out.println("Trade Executed: " + matchedQty + " shares of " + buy.getTicker()
                        + " at $" + String.format("%.2f", sell.getPrice()));

                buy.reduceQuantity(matchedQty);
                sell.reduceQuantity(matchedQty);

                // Check if buy and sell orders are fulfilled
                // Remove from queue if fulfilled
                if (buy.isFulfilled()) buys.poll();
                if (sell.isFulfilled()) sells.poll();
            } else {
                break;
            }
        }

        orderBook.finishMatching();
    }
}
