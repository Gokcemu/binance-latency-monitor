package com.quant.crypto.model;

/**
 * Classifies the network latency performance of the API connection.
 * Used to determine the health and speed of the data stream from the exchange.
 */
public enum RiskLevel {

    /**
     * Represents optimal network performance.
     * Latency is within the expected range (e.g., < 150ms) for real-time data processing.
     */
    NORMAL,

    /**
     * Represents a noticeable delay in data reception.
     * Indicates minor network congestion or server-side throttling (e.g., 150ms - 400ms).
     */
    WARNING,

    /**
     * Represents critical network lag or potential packet loss.
     * Latency is unacceptably high (e.g., > 400ms), risking stale data processing.
     */
    CRITICAL
}