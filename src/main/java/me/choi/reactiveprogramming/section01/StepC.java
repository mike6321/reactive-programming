package me.choi.reactiveprogramming.section01;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Flow.Publisher;

public class StepC {

    public static void main(String[] args) throws InterruptedException {
        // Publisher <- Observable
        // Subscriber <- Observer
        Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Publisher publisher = new Publisher() {
            @Override
            public void subscribe(Subscriber subscriber) {
                Iterator<Integer> iterator = iterable.iterator();
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        executorService.execute(() -> {
                            int i = 0;
                            try {
                                while (i++ < n) {
                                    if (iterator.hasNext()) {
                                        subscriber.onNext(iterator.next());
                                    } else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
                            } catch (RuntimeException e) {
                                subscriber.onError(e);
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> subscriber = new Subscriber<>() {
            Subscription subscription;
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println(Thread.currentThread().getName() + " onNext : " + item);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError" + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
        publisher.subscribe(subscriber);
        executorService.awaitTermination(10, TimeUnit.HOURS);
        executorService.shutdown();
    }

}
