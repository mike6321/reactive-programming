package me.choi.reactiveprogramming.section05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RemoteService {

    @RestController
    public static class MyController {

        private static final String URL1 = "/service1";
        private static final String URL2 = "/service2";

        @GetMapping(URL1)
        public String service1(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req+ "/service1";
        }

        @GetMapping(URL2)
        public String service2(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req+ "/service2";
        }

    }

    public static void main(String[] args) {
        System.setProperty("server.port", "8081");
        System.setProperty("server.tomcat.threads.max", "1000");
        SpringApplication.run(RemoteService.class, args);
    }

}
