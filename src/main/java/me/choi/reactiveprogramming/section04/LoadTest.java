package me.choi.reactiveprogramming.section04;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {

    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        RestTemplate restTemplate = new RestTemplate();
        final String url = "http://localhost:8080/rest?idx={idx}";

        CyclicBarrier barrier = new CyclicBarrier(101);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                int idx = counter.addAndGet(1);
                barrier.await();
                log.info("Thread: {}", idx);

                StopWatch subStopWatch = new StopWatch();
                subStopWatch.start();

                String res = restTemplate.getForObject(url, String.class, idx);

                subStopWatch.stop();
                log.info("Elapsed: {} {} / {}",idx, subStopWatch.getTotalTimeSeconds(), res);
                return null;
            });
        }
        barrier.await();
        StopWatch mainStopWatch = new StopWatch();
        mainStopWatch.start();
        executorService.shutdown();
        executorService.awaitTermination(100 , TimeUnit.SECONDS);

        mainStopWatch.stop();
        log.info("Total: {}", mainStopWatch.getTotalTimeSeconds());
    }

}
