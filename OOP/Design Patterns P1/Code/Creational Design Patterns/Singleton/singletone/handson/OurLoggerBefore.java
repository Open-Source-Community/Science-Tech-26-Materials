package designpattern.creational.singletone.handson;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

///  A logger should save in the same file
public class OurLoggerBefore {

    private String filePath;

    public OurLoggerBefore(String filePath) {
        this.filePath = filePath;
    }

    // Log message to a file
    public void log(String message) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(LocalDateTime.now() + " : " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class LoggerTestBefore{
    public static void main(String[] args) {
        OurLoggerBefore logger = new OurLoggerBefore("log.txt");
        logger.log("Hello World!");
    }
}