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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@SpringBootApplication
public class ReactiveProgrammingApplication03 {

    private final MyService myService;

    @Component
    public static class MyService {

        @Async("threadPoolExecutor")
        public ListenableFuture<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(1000);
            return new AsyncResult<>("Hello");
        }

    }

    @Bean
    public Executor threadPoolExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(100);
        threadPoolTaskExecutor.setQueueCapacity(200);
        threadPoolTaskExecutor.setThreadNamePrefix("my-thread");
        return threadPoolTaskExecutor;
    }

    public static void main(String[] args) {
        try(ConfigurableApplicationContext context = SpringApplication.run(ReactiveProgrammingApplication03.class, args)) {
        }
    }

    @Bean("applicationRunner03")
    public ApplicationRunner applicationRunner() {
        return args -> {
            log.info("applicationRunner()");
            ListenableFuture<String> future = myService.hello();
            future.addCallback(s -> System.out.println(s), e -> System.out.println(e));
            log.info("exit");
        };
    }

}
