package com.quant.crypto;

import com.quant.crypto.model.Trade;
import com.quant.crypto.service.BinanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Integration test for measuring network latency against the Binance API.
 * <p>
 * This test acts as a Quality Gate. If the network latency exceeds the defined threshold,
 * the test fails, simulating a "Go/No-Go" decision for deployment.
 * <p>
 * This replicates the "Duration Assertion" logic previously established in JMeter.
 */
class BinanceLatencyTest {

    private BinanceService service;

    // Maximum allowed latency in milliseconds (Threshold for test failure)
    // Adjusted to 500ms for a realistic Public Internet check.
    private final int MAX_ALLOWED_LATENCY = 500;

    /**
     * Sets up the test environment before each test method execution.
     */
    @BeforeEach
    void setUp() {
        service = new BinanceService();
    }

    /**
     * Fetches the latest trades and asserts that the network latency is within acceptable limits.
     */
    @Test
    @DisplayName("🚀 Performance Check: Latency must be within acceptable limits")
    void testNetworkLatency() {
        String symbol = "BTCUSDT";

        // 1. Action: Fetch recent trades from the API
        List<Trade> trades = service.getRecentTrades(symbol);

        // 2. Assertion: Verify that data was actually received
        Assertions.assertFalse(trades.isEmpty(), "❌ API returned an empty trade list! Check network connection.");

        // 3. Logic: Calculate Latency (Process Time - Event Time)
        // We take the first trade in the list assuming it's the most relevant/recent batch.
        Trade latestTrade = trades.get(0);

        long processingTime = System.currentTimeMillis();
        long eventTime = latestTrade.getTimestamp();
        long latency = processingTime - eventTime;

        // 4. Reporting: Print detailed benchmark results to the console (Standard Output)
        System.out.println("--------------------------------------------------");
        System.out.println("📊 LATENCY BENCHMARK RESULT");
        System.out.println("--------------------------------------------------");
        System.out.println("Symbol       : " + symbol);
        System.out.println("Event Time   : " + eventTime);
        System.out.println("Process Time : " + processingTime);
        System.out.println("Latency      : " + latency + " ms");
        System.out.println("Threshold    : " + MAX_ALLOWED_LATENCY + " ms");
        System.out.println("--------------------------------------------------");

        // 5. Assertion: Fail the test if latency exceeds the threshold
        // The message in the lambda function is constructed only if the assertion fails.
        Assertions.assertTrue(latency < MAX_ALLOWED_LATENCY,
                () -> "🚨 PERFORMANCE FAILURE! Latency (" + latency + "ms) exceeded the limit of " + MAX_ALLOWED_LATENCY + "ms");
    }
}