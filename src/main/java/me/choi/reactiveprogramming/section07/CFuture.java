package me.choi.reactiveprogramming.section07;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
                    if (1 == 1) {
                        throw new RuntimeException();
                    }
                    return 1;
                })
                .thenCompose((s) -> {
                    log.info("thenApply : {}", s);
                    return CompletableFuture.completedFuture(s + 1);
                })
                .thenApply((s) -> {
                    log.info("thenApply : {}", s);
                    return s * 3;
                })
                .exceptionally(e -> -10)
                .thenAccept(s -> log.info("thenAccept : {}", s));
        log.info("exit");
        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }

}
