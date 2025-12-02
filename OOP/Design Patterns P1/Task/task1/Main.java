package sciencetech.season25.designpatterns.p1.task1;

public class Main {

    public static void main(String[] args) {
        Database dbConfig = new Database();
        Server serverConfig = new Server();

        dbConfig.displayConfig();
        System.out.println();
        serverConfig.displayConfig();
    }
}
