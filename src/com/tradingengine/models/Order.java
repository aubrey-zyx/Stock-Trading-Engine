package com.tradingengine.models;

public class Order implements Comparable<Order> {
    public enum Type { BUY, SELL }
    private final Type type;
    private final String ticker;
    private int quantity;
    private final double price;
    private final long timestamp;

    public Order(Type type, String ticker, int quantity, double price) {
        this.type = type;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = System.nanoTime();
    }

    public Type getType() { return type; }
    public String getTicker() { return ticker; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public long getTimestamp() { return timestamp; }

    public void reduceQuantity(int matchedQty) { this.quantity -= matchedQty; }
    public boolean isFulfilled() { return quantity == 0; }

    @Override
    public int compareTo(Order o) {
        if (this.type == Type.BUY) {
            // Higher price first for BUY orders
            int priceComparison = Double.compare(o.price, this.price);
            if (priceComparison != 0) return priceComparison;
        } else {
            // Lower price first for SELL orders
            int priceComparison = Double.compare(this.price, o.price);
            if (priceComparison != 0) return priceComparison;
        }
        // If prices are the same, maintain FIFO ordering (earlier orders first)
        return Long.compare(this.timestamp, o.timestamp);
    }
}
