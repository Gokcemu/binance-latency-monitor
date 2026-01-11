package com.quant.crypto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single trade execution fetched from the Crypto Exchange API.
 * This class acts as a Data Transfer Object (DTO) for deserializing JSON responses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {

    /**
     * The trading pair symbol (e.g., BTCUSDT).
     * Note: This field is populated manually as it is not strictly part of the trade JSON object.
     */
    private String pair;

    @JsonProperty("price")
    private double price;

    @JsonProperty("qty")
    private double quantity;

    /**
     * Indicates if the buyer was the maker in the order book.
     * <p>
     * - If true: The buyer provided liquidity (Maker), so the aggressor (Taker) was the Seller. -> SIDE: SELL
     * - If false: The seller provided liquidity (Maker), so the aggressor (Taker) was the Buyer. -> SIDE: BUY
     */
    @JsonProperty("isBuyerMaker")
    private boolean isBuyerMaker;

    @JsonProperty("time")
    private long timestamp;

    /**
     * Default constructor required by Jackson for JSON deserialization.
     */
    public Trade() {}

    // --- Accessors ---

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getPair() {
        return pair;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Derives the side of the trade based on the maker flag.
     *
     * @return "SELL" if the trade was initiated by a seller, "BUY" otherwise.
     */
    public String getSide() {
        return isBuyerMaker ? "SELL" : "BUY";
    }

    @Override
    public String toString() {
        return String.format("[%s] Price: %.2f | Qty: %.4f | Side: %s",
                pair, price, quantity, getSide());
    }
}