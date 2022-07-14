package me.choi.reactiveprogramming.section04;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class FutureEx {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newCachedThreadPool();
//        step01(executorService);
//        step02(executorService);
//        step03(executorService);
//        step04(executorService);
    }

    private static void step01(ExecutorService executorService) throws InterruptedException, ExecutionException {
        executorService.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("async");
        });
        log.info("exit");
    }

    private static void step02(ExecutorService executorService) throws InterruptedException, ExecutionException {
        Future<String> future = executorService.submit(() -> {
            Thread.sleep(2000);
            log.info("async");
            return "Hello";
        });
        log.info("{}", future.get()); // blocking, non-blocking
        log.info("exit");
    }

    private static void step03(ExecutorService executorService) throws InterruptedException, ExecutionException {
        Future<String> future = executorService.submit(() -> {
            Thread.sleep(2000);
            log.info("async");
            return "Hello";
        });
        log.info("exit");
        log.info("{}", future.get()); // blocking, non-blocking
    }

    private static void step04(ExecutorService executorService) throws InterruptedException, ExecutionException {
        Future<String> future = executorService.submit(() -> {
            Thread.sleep(2000);
            log.info("async");
            return "Hello";
        });
        log.info("{}", future.isDone());
        Thread.sleep(2100);
        log.info("exit");
        log.info("{}", future.isDone());
        log.info("{}", future.get());
    }

}
