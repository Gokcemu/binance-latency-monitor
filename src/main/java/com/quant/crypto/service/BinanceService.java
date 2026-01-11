package com.quant.crypto.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quant.crypto.ConfigManager;
import com.quant.crypto.model.Trade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * Service class responsible for interacting with the Binance REST API.
 * Handles HTTP requests, JSON parsing, and error logging using Log4j2.
 */
public class BinanceService {

    private static final Logger logger = LogManager.getLogger(BinanceService.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BinanceService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches the most recent trades for a given symbol from the exchange.
     * Uses configuration settings for endpoints and limits.
     *
     * @param symbol The trading pair symbol (e.g., "BTCUSDT").
     * @return A list of {@link Trade} objects, or an empty list if the API call fails.
     */
    public List<Trade> getRecentTrades(String symbol) {
        // Retrieve settings from the configuration manager
        String baseUrl = ConfigManager.get("api.base.url");
        String endpoint = ConfigManager.get("api.trade.endpoint");
        String limit = ConfigManager.get("api.trade.limit");

        // Construct the full API URL
        String fullUrl = baseUrl + endpoint + "?limit=" + limit + "&symbol=" + symbol;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .GET()
                    .build();

            // Log the request at DEBUG level to avoid console noise in production
            logger.debug("📡 Sending Request to: {}", fullUrl);

            long start = System.currentTimeMillis();

            // Execute the HTTP Request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            long duration = System.currentTimeMillis() - start;

            // Handle non-200 responses (e.g., 429 Too Many Requests, 500 Server Error)
            if (response.statusCode() != 200) {
                logger.error("⛔ API Error! Status Code: {} | Response: {}", response.statusCode(), response.body());
                return Collections.emptyList();
            }

            logger.info("✅ API Response Received in {} ms", duration);

            // Deserialize JSON response to Trade objects
            List<Trade> trades = objectMapper.readValue(response.body(), new TypeReference<List<Trade>>() {});

            // Enrich the data: Set pair manually since API doesn't provide it inside the list
            trades.forEach(t -> t.setPair(symbol));

            return trades;

        } catch (Exception e) {
            logger.error("❌ Failed to fetch trades from Binance API", e);
            return Collections.emptyList();
        }
    }
}