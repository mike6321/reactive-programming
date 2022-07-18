package me.choi.reactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SpringBootApplication
public class ReactiveProgrammingApplication07 {

    @RestController
    public static class MyController {

        private static final String URL = "/rest";

        @GetMapping(URL)
        public String rest(int idx) {
            return "rest : " + idx;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication07.class, args);
    }

}
