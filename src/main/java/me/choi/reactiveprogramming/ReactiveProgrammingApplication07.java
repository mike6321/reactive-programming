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
        /**
         * 비동기 작업을 위해 백그라운드에 요청당 스레드를 생성한다.
         * (request 100 : -> thread : 100)
         * */

        @GetMapping(URL)
        public ListenableFuture<ResponseEntity<String>> rest(int idx) {
            return asyncRestTemplate.getForEntity("http://localhost:8081/service?req={req}",
                    String.class,
                    "hello" + idx);
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication07.class, args);
    }

}
