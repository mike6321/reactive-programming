package me.choi.reactiveprogramming;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@SpringBootApplication
public class ReactiveProgrammingApplication06 {

    @RestController
    public static class MyController {

        private static final String URL = "/emitter";
        private static final Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();

        @GetMapping(URL)
        public ResponseBodyEmitter emitter() throws InterruptedException {
            ResponseBodyEmitter emitter = new ResponseBodyEmitter();
            Executors.newSingleThreadExecutor()
                    .submit(() -> {
                        try {
                        for (int i = 0; i <= 50; i++) {
                                emitter.send("<p>Stream" + i + "</p>");
                                Thread.sleep(100);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
            return emitter;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication06.class, args);
    }

}
