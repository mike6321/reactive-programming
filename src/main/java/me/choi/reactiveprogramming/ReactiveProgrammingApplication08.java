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

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@SpringBootApplication
@EnableAsync
public class ReactiveProgrammingApplication08 {

    @RestController
    public static class MyController02 {

        private static final String URL = "/rest02";
        private static final String SUB_URL1 = "http://localhost:8081/service1?req={req}";
        private static final String SUB_URL2 = "http://localhost:8081/service2?req={req}";
        private final MyService myService;
        private AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(
                new Netty4ClientHttpRequestFactory(
                        new NioEventLoopGroup(1)
                )
        );

        public MyController02(MyService myService) {
            this.myService = myService;
        }

        @GetMapping(URL)
        public DeferredResult<String> rest(int idx) {
            DeferredResult<String> deferredResult = new DeferredResult<>();
            Completion
                    .from(
                    asyncRestTemplate.getForEntity(
                        SUB_URL1,
                        String.class,
                        "hello" + idx)
                    )
                    .andApply(
                        s -> asyncRestTemplate.getForEntity(
                        SUB_URL2,
                        String.class,
                        s.getBody())
                    )
                    .andAccept(s -> deferredResult.setResult(s.getBody()));
            return deferredResult;
        }

    }

    public static class AcceptCompletion extends Completion {

        private Consumer<ResponseEntity<String>> consumer;

        public AcceptCompletion(Consumer<ResponseEntity<String>> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void run(ResponseEntity<String> value) {
            consumer.accept(value);
        }

    }

    public static class ApplyCompletion extends Completion {

        private Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> function;

        public ApplyCompletion(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> function) {
            this.function = function;
        }

        @Override
        public void run(ResponseEntity<String> value) {
            ListenableFuture<ResponseEntity<String>> listenableFuture = function.apply(value);
            listenableFuture.addCallback(
                    s -> complete(s),
                    e -> error(e)
            );
        }
    }

    public static class Completion {

        private Completion next;

        public Completion() {
        }

        public void andAccept(Consumer<ResponseEntity<String>> consumer) {
            Completion completion = new AcceptCompletion(consumer);
            this.next = completion;
        }

        public Completion andApply(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> function) {
            Completion completion = new ApplyCompletion(function);
            this.next = completion;
            return completion;
        }

        public static Completion from(ListenableFuture<ResponseEntity<String>> listenableFuture) {
            Completion completion = new Completion();
            listenableFuture.addCallback(
                    completion::complete,
                    completion::error
            );
            return completion;
        }

        public void error(Throwable e) {

        }

        public void complete(ResponseEntity<String> s) {
            if (this.next != null) {
                this.next.run(s);
            }
        }

        public void run(ResponseEntity<String> value) {

        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication08.class, args);
    }

    @Service
    public static class MyService {
        @Async(value = "myThreadPool02")
        public ListenableFuture<String> work(String req) {
            return new AsyncResult<>(req + "/asyncwork");
        }
    }

    @Bean
    public ThreadPoolTaskExecutor myThreadPool02() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(1);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
