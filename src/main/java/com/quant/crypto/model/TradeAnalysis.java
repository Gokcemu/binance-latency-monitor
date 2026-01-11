package com.quant.crypto.model;

/**
 * Represents the result of a risk analysis performed on a specific trade.
 * This class is immutable to ensure the integrity of the analysis data.
 */
public class TradeAnalysis {

    private final Trade originalTrade;
    private final RiskLevel riskLevel;
    private final double totalValue;

    /**
     * Constructs a new analysis result.
     *
     * @param originalTrade The raw trade data fetched from the exchange.
     * @param riskLevel     The calculated risk classification (e.g., CRITICAL, NORMAL).
     * @param totalValue    The total volume of the trade in USD (Price * Quantity).
     */
    public TradeAnalysis(Trade originalTrade, RiskLevel riskLevel, double totalValue) {
        this.originalTrade = originalTrade;
        this.riskLevel = riskLevel;
        this.totalValue = totalValue;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public Trade getOriginalTrade() {
        return originalTrade;
    }

    public double getTotalValue() {
        return totalValue;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Volume: $%.2f",
                riskLevel, originalTrade.getPair(), totalValue);
    }
}