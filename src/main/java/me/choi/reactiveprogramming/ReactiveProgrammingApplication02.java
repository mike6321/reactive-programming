package me.choi.reactiveprogramming;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@SpringBootApplication
public class ReactiveProgrammingApplication02 {

    private final MyService myService;

    @Component
    public static class MyService {

        @Async
        public Future<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(1000);
            return new AsyncResult<>("Hello");
        }

    }

    public static void main(String[] args) {
        try(ConfigurableApplicationContext context = SpringApplication.run(ReactiveProgrammingApplication02.class, args)) {
        }
    }

    @Bean("applicationRunner02")
    public ApplicationRunner applicationRunner() {
        return args -> {
            log.info("applicationRunner()");
            Future<String> future = myService.hello();
            log.info("exit : {}", future.isDone());
            log.info("result : {}", future.get());
        };
    }

}
