package me.choi.reactiveprogramming.section07;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class CFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
                    return 1;
                })
                .thenCompose((s) -> {
                    log.info("thenApply : {}", s);
                    return CompletableFuture.completedFuture(s + 1);
                })
                .thenApplyAsync((s) -> {
                    log.info("thenApplyAsync : {}", s);
                    return s * 3;
                }, executorService)
                .exceptionally(e -> -10)
                .thenAcceptAsync(s -> log.info("thenAcceptAsync : {}", s), executorService);
        log.info("exit");
        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }

}
