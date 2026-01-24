package designpattern.creational.task1.solution.configuration_management;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/*Solution Summary
    First we make the ConfigManager a singleton to ensure that single global instance for configuration management
    Remember the thread safe and the performance trick with double check locking

    The ConfigManager reads the configurations from the config folder and before it's only read the files of .properties extension
    To Make it more extensible (as we read from task file):
        - First we need to think about what is the block of code that will be change based on extension file?
            - It's the logic of reading the file (.properties, XML, JSON have different formats) so the logic of reading also will be different
        - So we can create an interface called ConfigLoader that will have a method to load configurations
        - we restrict the access of all logic related to ConfigLoader to ConfigManager Package
        - the client now can use the ConfigManager to get the configurations based on the ConfigType
        - and now we can easily dealing with new file format by:
            - creating a new class that implements ConfigLoader for each format
            - adding a new ConfigType enum value for the new format
            - add new case in create switch in LoaderCreator (we can resolve it by factory pattern)
* */

public class ConfigManager {
    private static final String CONFIG_FOLDER_PATH = Paths.get("src", "main", "java", "designpattern", "creational", "task1", "config").toFile().getAbsolutePath();
    private final Map<String, Map<String, Object>> configurations;
    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private ConfigManager() {
        configurations = new HashMap<>();
    }

    public Map<String, Object> getConfigurations(String className, ConfigType configType) {
        ConfigLoader configLoader = LoaderCreator.create(configType);

        configurations.put(className, configLoader.loadConfigurations(new StringBuilder(CONFIG_FOLDER_PATH)
                .append(File.separator)
                .append(className)
                .append(".properties")
                .toString()));

        /// maybe we need to write some logic here

        return configurations.getOrDefault(className, new HashMap<>());
    }
}


