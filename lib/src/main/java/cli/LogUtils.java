package cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.LogManager.getLogManager;
import static java.util.logging.Logger.getLogger;

public class LogUtils {

    private static final String ENV_VAR = "CLI_LOG_LEVEL";

    public LogUtils() {
        try(InputStream stream = LogUtils.class.getResourceAsStream("/cli-logging.properties")) {
            getLogManager().readConfiguration(stream);
            // We can override log level by the env variable CLI_LOG_LEVEL
            // It can take any value in the java util logging Level enumeration

            var level = getLogLevelFromEnvironment();
            getLogger("cli").setLevel(level);
            // The following message will only be shown if log is fine enough
            getLogger("cli").log(
                Level.FINEST,
                String.format("Overridden log level to %s by env var %s", level, ENV_VAR)
            );
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    public static Logger getDefaultLogger() {
        return Logger.getLogger("cli");
    }

    static Level getLogLevelFromEnvironment() {
        var value = System.getenv(ENV_VAR);
        return (value != null && !value.trim().isEmpty())
            ? Level.parse(value.toUpperCase())
            : SEVERE;
    }
}
