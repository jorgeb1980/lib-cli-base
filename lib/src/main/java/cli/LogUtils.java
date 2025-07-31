package cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.LogManager.getLogManager;
import static java.util.logging.Logger.getLogger;

public class LogUtils {

    private static final String DEFAULT_LOGGING_CONFIGURATION = "/cli-logging.properties";
    private static final String CUSTOM_LOGGING_CONFIGURATION = "/custom-cli-logging.properties";

    private static final String ENV_VAR = "CLI_LOG_LEVEL";
    private static boolean logLevelSet = false;

    public LogUtils() {
        try(InputStream stream = getLoggingConfiguration()) {
            getLogManager().readConfiguration(stream);
            // We can override log level by the env variable CLI_LOG_LEVEL
            // It can take any value in the java util logging Level enumeration

            var level = getLogLevelFromEnvironment();
            if (level != null) {
                logLevelSet = true;
                getLogger("cli").setLevel(level);
                // The following message will only be shown if log is fine enough
                getLogger("cli").log(
                        Level.FINEST,
                        String.format("Overridden log level to %s by env var %s", level, ENV_VAR)
                );
            } else {
                getLogger("cli").setLevel(Level.ALL);
            }
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    private InputStream getLoggingConfiguration() {
        InputStream ret = null;
        try {
            ret = LogUtils.class.getResourceAsStream(CUSTOM_LOGGING_CONFIGURATION);
        } catch (Exception e) {
            ret = LogUtils.class.getResourceAsStream(DEFAULT_LOGGING_CONFIGURATION);
        }
        return ret;
    }

    public static Logger getDefaultLogger() {
        return Logger.getLogger("cli");
    }

    static Level getLogLevelFromEnvironment() {
        var value = System.getenv(ENV_VAR);
        return (value != null && !value.trim().isEmpty())
            ? Level.parse(value.toUpperCase())
            : null;
    }
}
