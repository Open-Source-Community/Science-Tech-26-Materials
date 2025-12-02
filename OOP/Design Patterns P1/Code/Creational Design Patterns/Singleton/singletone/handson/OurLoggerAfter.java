package designpattern.creational.singletone.handson;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

///  A logger should save in the same file
public class OurLoggerAfter {

    private static final String filePath = "log.txt";
    private static OurLoggerAfter instance;

    private OurLoggerAfter() {

    }

    public static OurLoggerAfter getInstance() {
        if( instance == null )
            instance = new OurLoggerAfter();
        return instance;
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


class LoggerTestAfter{
    public static void main(String[] args) {
        OurLoggerAfter logger = OurLoggerAfter.getInstance();
        logger.log("Hello World!");
    }
}