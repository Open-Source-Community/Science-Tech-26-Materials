package designpattern.creational.singletone.impl;

public class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {}

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
            System.out.println("Instance Created");
        }
        return instance;
    }
}

class TestLazySingleton{

    static void singleThreadTest(){
        LazySingleton singleton = LazySingleton.getInstance();
    }

    static void multiThreadTest(){
        Runnable r = () -> {
            System.out.println( "Thread : " + Thread.currentThread().getName() + " Started");
            LazySingleton.getInstance();
            System.out.println( "Thread : " + Thread.currentThread().getName() + " Ended");
        };
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();
    }
    public static void main(String[] args) {
        singleThreadTest();
//        multiThreadTest();
    }

}