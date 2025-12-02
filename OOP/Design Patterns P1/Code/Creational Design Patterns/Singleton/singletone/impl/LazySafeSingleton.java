package designpattern.creational.singletone.impl;

public class LazySafeSingleton {
    // volatile ensures changes are immediately visible to other threads
    private static volatile LazySafeSingleton instance;

    private LazySafeSingleton() {}

    public static LazySafeSingleton getInstance() {
            synchronized (LazySafeSingleton.class) {
                if (instance == null) {
                    instance = new LazySafeSingleton();
                }
            }
        return instance;
    }
}




class TestLazySafeSingleton{

    static void singleThreadTest(){
        LazySafeSingleton singleton = LazySafeSingleton.getInstance();
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
        singleThreadTest();
//        multiThreadTest();
    }

}