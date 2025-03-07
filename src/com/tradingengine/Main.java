package com.tradingengine;

import com.tradingengine.simulation.MarketSimulation;

public class Main {
    public static void main(String[] args) {
        MarketSimulation simulation = new MarketSimulation();
        simulation.startSimulation();
    }
}
