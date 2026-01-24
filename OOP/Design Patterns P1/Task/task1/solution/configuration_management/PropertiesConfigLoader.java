package designpattern.creational.task1.solution.configuration_management;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class PropertiesConfigLoader implements ConfigLoader {
    @Override
    public Map<String, Object> loadConfigurations(String filePath) {
        Map<String, Object> propertiesMap = new HashMap<>();
        File file = new File(filePath);

        try (FileReader reader = new FileReader(file)) {
            Properties properties = new Properties();
            properties.load(reader);
            for (String key : properties.stringPropertyNames())
                propertiesMap.put(key, properties.getProperty(key));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return propertiesMap;
    }
}
