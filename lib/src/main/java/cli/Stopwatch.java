package cli;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

class Stopwatch implements AutoCloseable {

    private final static Logger logger = Logger.getLogger(Stopwatch.class.getName());
    private Date initial;
    private String message;

    public Stopwatch(String message) {
        this.message = message;
        this.initial = new Date();
    }

    @Override
    public void close() throws CmdException {
        Date ending = new Date();
        logger.log(Level.FINE, message + " -> " + (ending.getTime() - initial.getTime()) + " mseg");
    }
}
