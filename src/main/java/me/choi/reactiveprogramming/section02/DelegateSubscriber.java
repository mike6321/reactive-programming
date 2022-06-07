package me.choi.reactiveprogramming.section02;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DelegateSubscriber implements Subscriber<Integer> {

    private final Subscriber subscriber;

    public DelegateSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(Integer i) {
        subscriber.onNext(i);
    }

    @Override
    public void onError(Throwable t) {
        subscriber.onError(t);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

}
