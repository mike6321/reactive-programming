package me.choi.reactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootApplication
public class ReactiveProgrammingApplication07 {

    @RestController
    public static class MyController {

        private static final String URL = "/rest";
        private RestTemplate restTemplate = new RestTemplate();

        @GetMapping(URL)
        public String rest(int idx) {
            String res = restTemplate.getForObject("http://localhost:8081//service?req={req}",
                    String.class,
                    "hello" + idx);
            return res;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication07.class, args);
    }

}
