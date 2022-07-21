package me.choi.reactiveprogramming;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;

@Slf4j
@SpringBootApplication
@EnableAsync
public class ReactiveProgrammingApplication09 {

    @RestController
    public static class MyController03 {

        private static final String URL = "/rest03";
        private final MyService myService;
        private static final String SUB_URL1 = "http://localhost:8081/service1?req={req}";
        private static final String SUB_URL2 = "http://localhost:8081/service2?req={req}";

        private AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(
                new Netty4ClientHttpRequestFactory(
                        new NioEventLoopGroup(1)
                )
        );

        public MyController03(MyService myService) {
            this.myService = myService;
        }

        @GetMapping(URL)
        public DeferredResult<String> rest(int idx) {
            DeferredResult<String> deferredResult = new DeferredResult<>();
            toCompletableFuture(
                    asyncRestTemplate.getForEntity(SUB_URL1,
                            String.class,
                            "hello" + idx)
            )
            .thenCompose(s ->
                        toCompletableFuture(
                                asyncRestTemplate.getForEntity(SUB_URL2,
                                        String.class,
                                        s.getBody()))
            )
            .thenCompose(s ->
                    toCompletableFuture(
                            myService.work(s.getBody())
                    )
            )
            .thenAccept(deferredResult::setResult)
            .exceptionally(e -> {
                deferredResult.setErrorResult(e.getMessage());
                return (Void) null;
            });
            return deferredResult;
        }

        private  <T> CompletableFuture<T> toCompletableFuture(ListenableFuture<T> listenableFuture) {
            CompletableFuture<T> completableFuture = new CompletableFuture<>();
            listenableFuture.addCallback(
                    completableFuture::complete,
                    completableFuture::completeExceptionally
            );
            return completableFuture;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication09.class, args);
    }

    @Service
    public static class MyService {
        @Async(value = "myThreadPool03")
        public ListenableFuture<String> work(String req) {
            return new AsyncResult<>(req + "/asyncwork");
        }
    }

    @Bean
    public ThreadPoolTaskExecutor myThreadPool03() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(1);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
