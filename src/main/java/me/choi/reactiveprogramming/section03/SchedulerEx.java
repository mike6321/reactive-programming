package me.choi.reactiveprogramming.section03;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SchedulerEx {

    public static void main(String[] args) {
        Publisher<Integer> publisher = subscriber -> {
            subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    log.debug("request");
                    subscriber.onNext(1);
                    subscriber.onNext(2);
                    subscriber.onNext(3);
                    subscriber.onNext(4);
                    subscriber.onNext(5);
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };

        Publisher<Integer> subOnPub = sub -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                protected String getDefaultThreadNamePrefix() {
                    return "subOn - ";
                }
            });
            executorService.execute(() -> publisher.subscribe(sub));
        };

        Publisher<Integer> pubOnPub = sub -> {
            subOnPub.subscribe(new Subscriber<Integer>() {
                ExecutorService executorService = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                    @Override
                    protected String getDefaultThreadNamePrefix() {
                        return "pubOn - ";
                    }
                });
                @Override
                public void onSubscribe(Subscription s) {
                    sub.onSubscribe(s);
                }

                @Override
                public void onNext(Integer integer) {
                    executorService.execute(() -> sub.onNext(integer));
                }

                @Override
                public void onError(Throwable t) {
                    executorService.execute(() -> sub.onError(t));
                }

                @Override
                public void onComplete() {
                    executorService.execute(() -> sub.onComplete());
                }
            });
        };

        pubOnPub.subscribe(new Subscriber<Integer>() {
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
        System.out.println("exit");
    }

}
