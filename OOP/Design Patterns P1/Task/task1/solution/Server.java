package designpattern.creational.task1.solution;

import designpattern.creational.task1.solution.configuration_management.ConfigManager;
import designpattern.creational.task1.solution.configuration_management.ConfigType;

import java.util.Map;


public class Server {
    private final String host;
    private final int port;

    public Server() {
        Map<String, Object> configs = ConfigManager.getInstance().getConfigurations("Server", ConfigType.Properties);
        this.host = (String) configs.getOrDefault("server.host", "localhost");
        this.port = Integer.parseInt((String) configs.getOrDefault("server.port", "8080"));
    }

    public void displayConfig() {
        System.out.println("Server Configuration:");
        System.out.println("\tHost: " + host);
        System.out.println("\tPort: " + port);
    }
}
