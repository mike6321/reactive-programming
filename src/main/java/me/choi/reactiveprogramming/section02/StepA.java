package me.choi.reactiveprogramming.section02;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reactive Streams : Operators
 *
 * Publisher -> [DATA 1] -> Operator1 -> [DATA 2] -> [Operator2] -> [DATA 3] -> Subscriber
 * 1. map (d1 -> f -> d2)
 * Publisher -> [DATA 1] -> MapPublisher -> [DATA 2] -> [Operator2] -> [DATA 3] -> Subscriber
 * */
@Slf4j
public class StepA {

    public static void main(String[] args) {
        Iterable<Integer> iterator = Stream.iterate(1, a -> a + 1)
                .limit(10)
                .collect(Collectors.toList());
        Publisher<Integer> publisher = iterPub(iterator);
        Publisher<Integer> mapPubFirst = mapPub(publisher, s -> s * 10);
        Publisher<Integer> mapPubSecond = mapPub(mapPubFirst, s -> s * -1);
        Publisher<Integer> sumPub = sumPub(mapPubSecond);
        sumPub.subscribe(logSub());
    }

    private static Publisher<Integer> sumPub(Publisher<Integer> publisher) {
        return subscriber -> publisher.subscribe(new DelegateSubscriber(subscriber) {
            int sum = 0;
            @Override
            public void onNext(Integer i) {
                sum += i;
            }

            @Override
            public void onComplete() {
                subscriber.onNext(sum);
                subscriber.onComplete();
            }
        });
    }

    private static Publisher<Integer> mapPub(Publisher<Integer> publisher, Function<Integer, Integer> function) {
        return subscriber -> publisher.subscribe(new DelegateSubscriber(subscriber) {
            @Override
            public void onNext(Integer i) {
                subscriber.onNext(function.apply(i));
            }
        });
    }

    private static Subscriber<Integer> logSub() {
        return new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {
                log.info("onNext :: {}", i);
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError :: {}", t);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(Iterable<Integer> iterator) {
        return subscriber -> subscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                try {
                    iterator.forEach(subscriber::onNext);
                    subscriber.onComplete();
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }
            }

            @Override
            public void cancel() {

            }
        });
    }

}
