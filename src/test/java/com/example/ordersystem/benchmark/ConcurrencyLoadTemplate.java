package com.example.ordersystem.benchmark;

import org.HdrHistogram.ConcurrentHistogram;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrencyLoadTemplate {

    public ConcurrencyLoadResult run(int requestCount, int threadCount, Runnable task) throws InterruptedException {

        AtomicInteger successCount = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(requestCount);
        ConcurrentHistogram latency = new ConcurrentHistogram(3);

        for(int i=0; i<requestCount; i++) {
            executorService.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    long requestBegin = System.nanoTime();
                    task.run();
                    successCount.incrementAndGet();
                    latency.recordValue(System.nanoTime() - requestBegin);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        assertThat(ready.await(30, TimeUnit.SECONDS)).isTrue();
        long begin = System.nanoTime();
        start.countDown();
        assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
        long entireTime = System.nanoTime() - begin;
        executorService.shutdown();

        return new ConcurrencyLoadResult(latency, entireTime, successCount);
    }
}
