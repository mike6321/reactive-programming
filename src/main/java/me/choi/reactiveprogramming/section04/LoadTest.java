package me.choi.reactiveprogramming.section04;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {

    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        RestTemplate restTemplate = new RestTemplate();
        final String url = "http://localhost:8080/callable";

        StopWatch mainStopWatch = new StopWatch();
        mainStopWatch.start();

        for (int i = 0; i < 100; i++) {
            executorService.execute(() -> {
                int idx = counter.addAndGet(1);
                log.info("Thread: {}", idx);

                StopWatch subStopWatch = new StopWatch();
                subStopWatch.start();

                restTemplate.getForObject(url, String.class);

                subStopWatch.stop();
                log.info("Elapsed: {} {}",idx, subStopWatch.getTotalTimeSeconds());

            });
        }
        executorService.shutdown();
        executorService.awaitTermination(100 , TimeUnit.SECONDS);

        mainStopWatch.stop();
        log.info("Total: {}", mainStopWatch.getTotalTimeSeconds());
    }

}
