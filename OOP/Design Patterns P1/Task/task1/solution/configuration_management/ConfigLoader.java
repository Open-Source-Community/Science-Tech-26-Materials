package designpattern.creational.task1.solution.configuration_management;

import java.util.Map;

/**
 * Interface for loading configurations to be the ConfigManager more extensible
 */
interface ConfigLoader {
    Map<String, Object> loadConfigurations(String filePath);
}
