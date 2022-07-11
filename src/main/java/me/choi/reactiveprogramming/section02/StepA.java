package me.choi.reactiveprogramming.section02;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class StepA {

    public static void main(String[] args) {
        Iterable<Integer> iterator = Stream.iterate(1, a -> a + 1)
                .limit(10)
                .collect(Collectors.toList());
        Publisher<Integer> publisher = iterPub(iterator);
//        Publisher<String> mapPub = mapPub(publisher, s -> "[" + s + "]");
//        mapPub.subscribe(logSub());
        Publisher<StringBuilder> reducePub = reducePub(publisher, new StringBuilder(),
                (a, b) -> a.append(b + ","));
        reducePub.subscribe(logSub());
    }

    private static <T, R> Publisher<R> mapPub(Publisher<T> publisher, Function<T, R> function) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> subscriber) {
                publisher.subscribe(new DelegateSubscriber<T, R>(subscriber) {
                    @Override
                    public void onNext(T i) {
                        subscriber.onNext(function.apply(i));
                    }
                });
            }
        };
    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> publisher, R init, BiFunction<R, T, R> biFunction) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> subscriber) {
                publisher.subscribe(new DelegateSubscriber<T, R>(subscriber) {
                    R result = init;
                    @Override
                    public void onNext(T i) {
                        result = biFunction.apply(result, i);;
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(result);
                        subscriber.onComplete();
                    }
                });
            }
        };
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T i) {
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
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
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
        };
    }

}
