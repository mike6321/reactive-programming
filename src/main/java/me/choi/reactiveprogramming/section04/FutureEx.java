package me.choi.reactiveprogramming.section04;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
public class FutureEx {

    @FunctionalInterface
    interface SuccessCallback {
        void onSuccess(String result);
    }

    @FunctionalInterface
    interface ExceptionCallback {
        void onError(Throwable throwable);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback successCallback;
        ExceptionCallback exceptionCallback;

        public CallbackFutureTask(Callable<String> callable,
                                  SuccessCallback successCallback,
                                  ExceptionCallback exceptionCallback) {
            super(callable);
            this.successCallback = Objects.requireNonNull(successCallback);
            this.exceptionCallback = Objects.requireNonNull(exceptionCallback);
        }

        @Override
        protected void done() {
            try {
                successCallback.onSuccess(get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                exceptionCallback.onError(e.getCause());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newCachedThreadPool();
//        step01(executorService);
//        step02(executorService);
//        step03(executorService);
//        step04(executorService);
//        step05(executorService);
//        step06(executorService);
        step07(executorService);
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

    private static void step05(ExecutorService executorService) throws InterruptedException, ExecutionException {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            Thread.sleep(2000);
            log.info("async");
            return "Hello";
        });
        executorService.execute(futureTask);
        log.info("{}", futureTask.isDone());
        Thread.sleep(2100);
        log.info("exit");
        log.info("{}", futureTask.isDone());
        log.info("{}", futureTask.get());
    }

    private static void step06(ExecutorService executorService) throws InterruptedException, ExecutionException {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            Thread.sleep(2000);
            log.info("async");
            return "Hello";
        }) {
            @Override
            protected void done() {
                try {
                    log.info("{}", get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        executorService.execute(futureTask);
        executorService.shutdown();
    }

    private static void step07(ExecutorService executorService) {
        CallbackFutureTask callbackFutureTask = new CallbackFutureTask
                (
                        () -> {
                            Thread.sleep(2000);
                            log.info("async");
                            // 강제 에러
                            if (1 == 1) {
                                throw new RuntimeException("Async Error");
                            }
                            return "Hello";
                        },
                        s -> System.out.println("Result : " + s),
                        e -> System.out.println("Error : " + e.getMessage())
                );
        executorService.execute(callbackFutureTask);
        executorService.shutdown();
    }

}
