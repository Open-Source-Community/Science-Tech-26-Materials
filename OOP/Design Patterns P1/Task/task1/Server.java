package sciencetech.season25.designpatterns.p1.task1;

import java.util.Map;


public class Server {

    private final String host;
    private final int port;


    public Server() {
        Map<String, Object> configs = new ConfigManager().getConfigurations("Server");
        this.host = (String) configs.getOrDefault("server.host", "localhost");
        this.port = Integer.parseInt((String) configs.getOrDefault("server.port", "8080"));
    }


    public void displayConfig() {
        System.out.println("Server Configuration:");
        System.out.println("\tHost: " + host);
        System.out.println("\tPort: " + port);
    }
}
