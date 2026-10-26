package com.example.ordersystem.benchmark;

import org.HdrHistogram.Histogram;

import java.util.concurrent.atomic.AtomicInteger;

public record ConcurrencyLoadResult(Histogram latency, long entireTime, int successCount) {

    public double tps() {
        return successCount / (entireTime / 1_000_000_000.0);
    }
}
