package me.choi.reactiveprogramming;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
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

@Slf4j
@SpringBootApplication
@EnableAsync
public class ReactiveProgrammingApplication07 {

    @RestController
    public static class MyController01 {

        private static final String URL = "/rest01";
        private final MyService myService;
        private AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(
                new Netty4ClientHttpRequestFactory(
                        new NioEventLoopGroup(1)
                )
        );

        public MyController01(MyService myService) {
            this.myService = myService;
        }

        @GetMapping(URL)
        public DeferredResult<String> rest(int idx) {
            DeferredResult<String> deferredResult = new DeferredResult<>();
            ListenableFuture<ResponseEntity<String>> listenableFuture1 = asyncRestTemplate.getForEntity("http://localhost:8081/service1?req={req}",
                    String.class,
                    "hello" + idx);
            listenableFuture1.addCallback(
                    s -> {
                        ListenableFuture<ResponseEntity<String>> listenableFuture2 = asyncRestTemplate.getForEntity("http://localhost:8081/service2?req={req}",
                                String.class,
                                s.getBody());
                        listenableFuture2.addCallback(
                                s2 -> {
                                    ListenableFuture<String> listenableFuture3 = myService.work(s2.getBody());
                                    listenableFuture3.addCallback(
                                            s3 -> deferredResult.setResult(s3),
                                            e3 -> deferredResult.setErrorResult(e3.getMessage())
                                    );
                                },
                                e2 -> deferredResult.setErrorResult(e2.getMessage())
                        );
                    },
                    e -> deferredResult.setErrorResult(e.getMessage())
            );
            return deferredResult;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication07.class, args);
    }

    @Service
    public static class MyService {
        @Async(value = "myThreadPool01")
        public ListenableFuture<String> work(String req) {
            return new AsyncResult<>(req + "/asyncwork");
        }
    }

    @Bean
    public ThreadPoolTaskExecutor myThreadPool01() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(1);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
