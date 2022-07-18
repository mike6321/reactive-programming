package me.choi.reactiveprogramming;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
@SpringBootApplication
public class ReactiveProgrammingApplication07 {

    @RestController
    public static class MyController {

        private static final String URL = "/rest";
        private AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(
                new Netty4ClientHttpRequestFactory(
                        new NioEventLoopGroup(1)
                )
        );

        @GetMapping(URL)
        public DeferredResult<String> rest(int idx) {
            DeferredResult<String> deferredResult = new DeferredResult<>();
            ListenableFuture<ResponseEntity<String>> listenableFuture = asyncRestTemplate.getForEntity("http://localhost:8081/service?req={req}",
                    String.class,
                    "hello" + idx);
            listenableFuture.addCallback(
                    s -> deferredResult.setResult(s.getBody() + "/work"),
                    e -> deferredResult.setResult(e.getMessage())
            );
            return deferredResult;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication07.class, args);
    }

}
