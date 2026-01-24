package designpattern.creational.task1.solution.configuration_management;

class LoaderCreator {
    static ConfigLoader create(ConfigType configType) {
        return switch(configType){
            case Properties -> new PropertiesConfigLoader();
            default -> throw new IllegalArgumentException(configType.name() + " is not supported");
        };
    }
}
