package me.choi.reactiveprogramming;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@SpringBootApplication
public class ReactiveProgrammingApplication04 {

    @RestController
    public static class MyController {

        @GetMapping("/callable")
        public String async() throws InterruptedException {
            log.info("async");
            Thread.sleep(2000);
            return "hello";
        }

//        @GetMapping("/callable")
//        public Callable<String> async() throws InterruptedException {
//            log.info("callable");
//            return () -> {
//                log.info("async");
//                Thread.sleep(2000);
//                return "hello";
//            };
//        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication04.class, args);
    }

}
