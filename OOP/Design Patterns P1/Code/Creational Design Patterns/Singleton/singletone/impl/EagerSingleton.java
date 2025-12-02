package designpattern.creational.singletone.impl;

public class EagerSingleton {
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    private EagerSingleton() {
    }

    public static EagerSingleton getInstance() {
        System.out.println("EagerSingleton: Getting instance");
        return INSTANCE;
    }

    public static void doSomething(){
        System.out.println("EagerSingleton: Doing Something");
    }
}


class TestEagerSingleton{

    static void singleThreadTest(){
        System.out.println("Before Get Instance");
        EagerSingleton singleton = EagerSingleton.getInstance();
    }

    static void multiThreadTest(){
        Runnable r = () -> {
            System.out.println( "Thread : " + Thread.currentThread().getName() + " Started");
            EagerSingleton.getInstance();
            System.out.println( "Thread : " + Thread.currentThread().getName() + " Ended");
        };
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();
    }
    public static void main(String[] args) {
        EagerSingleton.doSomething();
        singleThreadTest();
//        multiThreadTest();
    }

}