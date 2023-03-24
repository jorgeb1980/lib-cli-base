package cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogUtils {

    private static final String ENV_VAR = "CLI_LOG_LEVEL";

    public LogUtils() {
        try(InputStream stream = LogUtils.class.getResourceAsStream("/cli-logging.properties")) {
            LogManager.getLogManager().readConfiguration(stream);
            // We can override log level by the env variable CLI_LOG_LEVEL
            // It can take any value in the java util logging Level enumeration
            String value = System.getenv(ENV_VAR);
            var level = (value != null && value.trim().length() > 0)
                ? Level.parse(value.toUpperCase())
                : null;
            if (level != null) {
                Logger.getLogger("cli").setLevel(level);
                // The following message will only be shown if log is fine enough
                Logger.getLogger("cli").log(
                    Level.FINEST,
                    String.format("Overridden log level to %s by env var %s", level, ENV_VAR)
                );
            }
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }


}
