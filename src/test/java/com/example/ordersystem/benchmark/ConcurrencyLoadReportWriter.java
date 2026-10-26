package com.example.ordersystem.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConcurrencyLoadReportWriter {

    static void write(ConcurrencyLoadResult result, ConcurrencyLoadCondition condition) throws IOException {
        Path dirPath = Path.of("build", "reports", "benchmark");
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve("result.csv");
        if(Files.notExists(filePath)) {
            Files.writeString(filePath, "전략,스레드 수,상품 수,p50_ms,p95_ms,p99_ms,tps\n");
        }

        String line = String.join(
                ",",
                condition.getStrategy(),
                String.valueOf(condition.getThreadCount()),
                String.valueOf(condition.getProductCount()),
                String.valueOf(result.latency().getValueAtPercentile(50) / 1000.0),
                String.valueOf(result.latency().getValueAtPercentile(95) / 1000.0),
                String.valueOf(result.latency().getValueAtPercentile(99) / 1000.0),
                String.format("%.3f", result.tps())
                );
        Files.writeString(filePath, line + "\n", StandardOpenOption.APPEND);
    }
}
