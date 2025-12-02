package designpattern.creational.singletone;

class SingletonClass{
    private static SingletonClass instance ;
    private SingletonClass(){}
    public static SingletonClass getInstance(){
        if(instance == null){
            System.out.println("Creating Instance");
            instance = new SingletonClass();
        }
        return instance;
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Main Thread Started.");
        Runnable p1 = () -> {
            System.out.println("Staring Process 1:" + Thread.currentThread().getName());
            SingletonClass.getInstance();
            System.out.println("Process 1 Completed.");
        };

        Runnable p2 = () -> {
            System.out.println("Staring Process 2:" + Thread.currentThread().getName());
            SingletonClass.getInstance();
            System.out.println("Process 2 Completed.");
        };

        Thread th1 = new Thread(p1);
        Thread th2 = new Thread(p2);
        th1.start();
        th2.start();
        System.out.println("Main Thread Ended.");
    }
}