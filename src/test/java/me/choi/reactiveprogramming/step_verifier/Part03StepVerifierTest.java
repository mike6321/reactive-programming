package me.choi.reactiveprogramming.step_verifier;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;


class Part03StepVerifierTest {

    // TODO: 2022/06/28 Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements then completes successfully.
    @Test
    void step01() {
        Flux<String> flux = Flux.just("foo", "bar");
        StepVerifier.create(flux)
                .expectNext("foo")
                .expectNext("bar")
                .verifyComplete();
    }

    // TODO: 2022/06/28 Use StepVerifier to check that the flux parameter emits a User with "swhite"username and another one with "jpinkman" then completes successfully.
    @Test
    void step02() {
        Flux<User> userFlux = Flux.just(new User("swhite"),
                new User("jpinkman"));
        StepVerifier.create(userFlux)
                .assertNext(u -> assertThat(u.getUsername()).isEqualTo("swhite"))
                .assertNext(u -> assertThat(u.getUsername()).isEqualTo("jpinkman"))
                .verifyComplete();
    }

    // TODO: 2022/06/28 Expect 10 elements then complete and notice how long the test takes.
    @Test
    void step03() {
        Flux<Long> take10 = Flux.interval(Duration.ofMillis(100))
                .take(10);
        StepVerifier.create(take10)
                .expectNextCount(10)
                .verifyComplete();
    }

    public static class User {

        private String username;

        public User(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

    }

}
