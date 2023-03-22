package cli;

import java.util.Date;
import java.util.logging.*;

public class LogUtils {

    private final static Level EXPECTED_LEVEL = Level.FINEST;

    public static void setupLogs() {
        try {
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            rootLogger.setLevel(EXPECTED_LEVEL);
            SimpleFormatter simpleFormatter = new SimpleFormatter() {
                @Override
                public String format(LogRecord lr) {
                    return String.format(
                        "[%1$tF %1$tT] [%2$-7s] %3$s %n",
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                    );
                }
            };
            Handler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(simpleFormatter);
            consoleHandler.setLevel(EXPECTED_LEVEL);
            rootLogger.addHandler(consoleHandler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
