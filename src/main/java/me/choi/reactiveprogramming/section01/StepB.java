package me.choi.reactiveprogramming.section01;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("deprecation")
public class StepB {

    static class IntObservable extends Observable implements Runnable {

        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) {
                setChanged();
                notifyObservers(i); // push
                // int i = iterable.next(); // pull
            }
        }

    }
    /**
     * main EXIT
     * pool-1-thread-1 1
     * pool-1-thread-1 2
     * pool-1-thread-1 3
     * pool-1-thread-1 4
     * pool-1-thread-1 5
     * pool-1-thread-1 6
     * pool-1-thread-1 7
     * pool-1-thread-1 8
     * pool-1-thread-1 9
     * pool-1-thread-1 10
     * */
    public static void main(String[] args) {
        // Source -> Event / Data -> Observer
        Observer observer = (o, arg) -> System.out.println(Thread.currentThread().getName() + " " + arg);
        IntObservable io = new IntObservable();
        io.addObserver(observer);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(io);
        System.out.println(Thread.currentThread().getName() + " EXIT");
        executorService.shutdown();
//        io.run();
    }
    /**
     * https://www.reactive-streams.org/
     * 기존 Observer의 단점
     *  1. complete 개념이 없다.
     *  2. Exception 처리
     * */

}
