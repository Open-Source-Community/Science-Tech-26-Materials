package designpattern.creational.singletone.impl;

public class StaticInnerClassSingleton {
    private StaticInnerClassSingleton() {
        System.out.println( "Creating Singleton Instance");
    }

    private static class SingletonHelper {
        private static final StaticInnerClassSingleton INSTANCE = new StaticInnerClassSingleton();
    }

    public static StaticInnerClassSingleton getInstance() {
        System.out.println(">>> Getting Singleton Instance");
        return SingletonHelper.INSTANCE;
    }

    public static void doSomething(){
        System.out.println("EagerSingleton: Doing Something");
    }
}


class TestStaticInnerClassSingleton{

    static void singleThreadTest(){
        StaticInnerClassSingleton singleton = StaticInnerClassSingleton.getInstance();
    }

    static void multiThreadTest(){
        Runnable r = () -> {
            System.out.println( "Thread : " + Thread.currentThread().getName() + " Started");
            StaticInnerClassSingleton.getInstance();
            System.out.println( "Thread : " + Thread.currentThread().getName() + " Ended");
        };
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();
    }
    public static void main(String[] args) {
        StaticInnerClassSingleton.doSomething();
        singleThreadTest();
//        multiThreadTest();
    }

}