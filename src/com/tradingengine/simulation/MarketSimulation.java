package com.tradingengine.simulation;

import com.tradingengine.engine.TradingEngine;
import com.tradingengine.models.Order;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates a real-world trading environment with multiple stockbrokers.
 */
public class MarketSimulation {
    private final TradingEngine engine = new TradingEngine();
    private final String[] tickers = {"AAPL", "GOOG", "TSLA", "MSFT", "AMZN"};

    public void startSimulation() {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                while (true) {
                    Order.Type type = ThreadLocalRandom.current().nextBoolean() ? Order.Type.BUY : Order.Type.SELL;
                    String ticker = tickers[ThreadLocalRandom.current().nextInt(tickers.length)];
                    int quantity = ThreadLocalRandom.current().nextInt(1, 101);
                    double price = ThreadLocalRandom.current().nextDouble(10.0, 500.0);

                    engine.addOrder(type, ticker, quantity, price);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }).start();
        }
    }
}
