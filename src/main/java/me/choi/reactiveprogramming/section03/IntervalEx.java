package me.choi.reactiveprogramming.section03;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class IntervalEx {

    public static void main(String[] args) {
        Publisher<Integer> publisher = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    int no = 0;
                    boolean cancelled = false;
                    @Override
                    public void request(long n) {
                        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                        exec.scheduleAtFixedRate(() -> {
                            if (cancelled) {
                                exec.shutdown();
                                return;
                            }
                            subscriber.onNext(no++);
                        }, 0, 300, TimeUnit.MILLISECONDS);
                    }

                    @Override
                    public void cancel() {
                        cancelled = true;
                    }
                });
            }
        };

        Publisher<Integer> takePub = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                publisher.subscribe(new Subscriber<Integer>() {
                    int count = 0;
                    Subscription subSc;
                    @Override
                    public void onSubscribe(Subscription s) {
                        subSc = s;
                        subscriber.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        subscriber.onNext(integer);
                        if (++count >= 5) {
                            subSc.cancel();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        subscriber.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onComplete();
                    }
                });
            }
        };

        takePub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext : {}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError : {}", t);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        });
    }

}
