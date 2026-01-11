package com.quant.crypto;

import com.quant.crypto.model.RiskLevel;
import com.quant.crypto.model.Trade;
import com.quant.crypto.model.TradeAnalysis;
import com.quant.crypto.service.BinanceService; // Service paketinden import ettik
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Main application entry point for the Crypto Latency Monitor.
 * <p>
 * This application connects to the Binance API, streams real-time trade data,
 * and analyzes network latency to detect connectivity issues or server-side throttling.
 */
public class App {

    // Initialize Log4j Logger
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        BinanceService service = new BinanceService();

        // Load configuration settings
        String symbol = ConfigManager.get("api.default.symbol");
        int criticalThreshold = ConfigManager.getInt("latency.threshold.critical");
        int warningThreshold = ConfigManager.getInt("latency.threshold.warning");

        logger.info("🚀 SYSTEM STARTED: Monitoring Network Latency for [{}]", symbol);
        logger.info("⚙️ CONFIGURATION: Warning > {}ms | Critical > {}ms", warningThreshold, criticalThreshold);

        // Infinite Loop for Continuous Monitoring
        while (true) {
            try {
                // Fetch the latest trades from the exchange
                List<Trade> trades = service.getRecentTrades(symbol);

                for (Trade trade : trades) {
                    // 1. Capture Timestamps
                    long processingTime = System.currentTimeMillis(); // When we processed it
                    long eventTime = trade.getTimestamp();            // When it actually happened

                    // 2. Calculate Latency (Lag)
                    long latency = processingTime - eventTime;

                    // 3. Determine Risk Level based on Latency
                    RiskLevel riskLevel;
                    if (latency > criticalThreshold) {
                        riskLevel = RiskLevel.CRITICAL;
                    } else if (latency > warningThreshold) {
                        riskLevel = RiskLevel.WARNING;
                    } else {
                        riskLevel = RiskLevel.NORMAL;
                    }

                    // 4. Create Analysis Object (Optional: Can be sent to a database later)
                    TradeAnalysis analysis = new TradeAnalysis(trade, riskLevel, latency);

                    // 5. Intelligent Logging (Focus on Anomalies)
                    if (riskLevel == RiskLevel.CRITICAL) {
                        // Log detailed timing info for debugging high latency
                        logger.error("🚨 HIGH LATENCY: {} ms | Event Time: {} | Process Time: {}",
                                latency, eventTime, processingTime);
                    } else if (riskLevel == RiskLevel.WARNING) {
                        logger.warn("⚠️ NETWORK LAG: {} ms", latency);
                    } else {
                        // Debug level avoids flooding the console during normal operation
                        logger.debug("✅ Stable Connection: {} ms", latency);
                    }
                }

                // Sleep to prevent API Rate Limiting (HTTP 429)
                // A safe interval for public APIs is usually 1-2 seconds.
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                logger.error("❌ Monitor interrupted! Shutting down...", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("❌ Unexpected error in monitoring loop", e);
            }
        }
    }
}