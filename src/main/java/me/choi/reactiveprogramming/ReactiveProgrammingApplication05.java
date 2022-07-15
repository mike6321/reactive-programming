package me.choi.reactiveprogramming;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@SpringBootApplication
public class ReactiveProgrammingApplication05 {

    @RestController
    public static class MyController {

        private static final String URL = "/deferred-result";
        private static final Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();

        @GetMapping(URL)
        public DeferredResult<String> callable() throws InterruptedException {
            log.info("deferred-result");
            DeferredResult<String> deferredResult = new DeferredResult<>();
            results.add(deferredResult);
            return deferredResult;
        }

        @GetMapping(URL + "/count")
        public String deferredResultCount() {
            return String.valueOf(results.size());
        }

        @GetMapping(URL + "/event")
        public String deferredResultEvent(String message) {
            for (DeferredResult<String> deferredResult : results) {
                deferredResult.setResult("Hello " + message);
                results.remove(deferredResult);
            }
            return "OK";
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication05.class, args);
    }

}
