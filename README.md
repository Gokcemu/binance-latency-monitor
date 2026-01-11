# Binance Data Latency Analyzer

![Java](https://img.shields.io/badge/Java-21%20LTS-orange)
![Build](https://img.shields.io/badge/Maven-3.8-lightgrey)
![License](https://img.shields.io/badge/License-MIT-green)

## Overview
This project serves as a Proof of Concept (PoC) to analyze network latency discrepancies in financial data retrieval. It specifically targets the latency gap between the **Exchange Event Time** and the **Local Processing Time**.

The primary goal is to demonstrate the limitations of **REST API Polling** architectures for High-Frequency Trading (HFT) scenarios, providing empirical evidence for the necessity of WebSocket-based implementations.

## Key Findings: REST vs. Real-Time
Benchmarking results from this application highlight significant data staleness inherent to polling mechanisms:

* **Network Round-Trip:** ~300ms average on standard broadband.
* **Data Staleness:** Observed delays up to **2000ms** due to bulk response structures.
* **Conclusion:** REST APIs provide "snapshot" data which is often stale upon arrival. For real-time execution, an event-driven WebSocket architecture is required to eliminate polling overhead.

## Technical Architecture
The application is built using a modular microservice-compatible structure on the latest LTS Java version:

* **Core:** Java 21 (OpenJDK) - *Selected for Virtual Threads (Project Loom) readiness.*
* **Networking:** `java.net.http.HttpClient` (Non-blocking I/O)
* **Data Parsing:** Jackson (`fasterxml`) for high-performance JSON deserialization.
* **Logging:** Apache Log4j 2 for asynchronous, level-based logging.
* **Architecture:** Separated concerns (Service, Model, Configuration layers).

## Usage & Configuration

### Prerequisites
* Java 21 or higher
* Maven 3.6+

### Configuration
The application behavior can be tuned via `src/main/resources/application.properties`:

```properties
# Target Asset
api.default.symbol=BTCUSDT

# Latency Thresholds (in Milliseconds)
latency.threshold.critical=200
latency.threshold.warning=100
```
## Clone the repository
git clone [https://github.com/Gokcemu/binance-latency-monitor.git](https://github.com/YOUR_USERNAME/binance-latency-monitor.git)

## Build the project
mvn clean install

##  Run the Experiment (Prove the Latency):
This command will trigger the BinanceLatencyTest and likely result in a 
BUILD FAILURE, proving the high latency.

mvn test

## Build the Application (Skip Tests): 
If you want to build the executable jar without running the latency assertion:

mvn clean install -DskipTests

## Scientific Proof (The Intentional Failure)
The unit test BinanceLatencyTest enforces a strict 500ms threshold. 
The failure below serves as evidence that REST Polling cannot meet HFT requirements.

```properties
[ERROR] Failures:
[ERROR]   BinanceLatencyTest.testNetworkLatency:71
🚨 PERFORMANCE FAILURE! Latency (4568ms) exceeded the limit of 500ms
```

## Run the application
java -cp target/classes:target/dependency/* com.quant.crypto.App

## Application Runtime Logs
```properties
INFO  com.quant.crypto.App - 🚀 SYSTEM STARTED: Monitoring Network Latency for [BTCUSDT]
INFO  com.quant.crypto.App - ⚙️ CONFIGURATION: Warning > 100ms | Critical > 200ms
INFO  com.quant.crypto.service.BinanceService - ✅ API Response Received in 537 ms
ERROR com.quant.crypto.App - 🚨 HIGH LATENCY: 207 ms | Event Time: 1768143272970 | Process Time: 1768143273177
ERROR com.quant.crypto.App - 🚨 HIGH LATENCY: 207 ms | Event Time: 1768143272970 | Process Time: 1768143273177
WARN  com.quant.crypto.App - ⚠️ NETWORK LAG: 137 ms
WARN  com.quant.crypto.App - ⚠️ NETWORK LAG: 137 ms
```
