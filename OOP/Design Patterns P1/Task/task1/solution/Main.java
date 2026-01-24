package designpattern.creational.task1.solution;

public class Main {

    public static void main(String[] args) {
        Database dbConfig = new Database();
        Server serverConfig = new Server();

        dbConfig.displayConfig();
        System.out.println();
        serverConfig.displayConfig();
    }
}
