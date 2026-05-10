package cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.LogManager.getLogManager;
import static java.util.logging.Logger.getLogger;

public class LogUtils {

    private static final String DEFAULT_LOGGING_CONFIGURATION = "/cli-logging.properties";
    private static final String CUSTOM_LOGGING_CONFIGURATION = "/custom-cli-logging.properties";

    private static final String ENV_VAR = "CLI_LOG_LEVEL";

    public LogUtils() {
        try(InputStream stream = getLoggingConfiguration()) {
            getLogManager().readConfiguration(stream);
            // We can override log level by the env variable CLI_LOG_LEVEL
            // It can take any value in the java util logging Level enumeration

            var level = getLogLevelFromEnvironment();
            if (level != null) {
                getLogger("cli").setLevel(level);
                // The following message will only be shown if log is fine enough
                getLogger("cli").log(
                        Level.FINEST,
                        String.format("Overridden log level to %s by env var %s", level, ENV_VAR)
                );
            } else {
                if (getLogger("cli").getLevel() == null) {
                    // If the client app did not define a log level, we assume that the default logger is meant to let
                    //  everything pass, and we rely on each specific handler level
                    getLogger("cli").setLevel(Level.ALL);
                }
            }
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    private static class SafeFileHandler extends FileHandler {

        public SafeFileHandler() throws IOException, SecurityException {
            super();
        }
        public SafeFileHandler(String path) throws SecurityException, IOException {
            super(path);
        }
        public SafeFileHandler(String path, boolean append) throws SecurityException, IOException {
            super(path, append);
        }
        public SafeFileHandler(String pattern, int limit, int count) throws SecurityException, IOException {
            super(pattern, limit, count);
        }
        public SafeFileHandler(String pattern, int limit, int count, boolean append) throws SecurityException, IOException {
            super(pattern, limit, count, append);
        }
        public SafeFileHandler(String pattern, long limit, int count, boolean append) throws SecurityException, IOException {
            super(pattern, limit, count, append);
        }

        @Override
        public void close() throws SecurityException {
            super.close();
        }
        @Override
        public void flush() {
            super.flush();
        }

    }

    private InputStream getLoggingConfiguration() {
        InputStream ret = null;
        ret = LogUtils.class.getResourceAsStream(CUSTOM_LOGGING_CONFIGURATION);
        if (ret == null) ret = LogUtils.class.getResourceAsStream(DEFAULT_LOGGING_CONFIGURATION);
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
