package cli;

import java.util.Date;

class Stopwatch implements AutoCloseable {

    private Date initial;
    private String message;

    public Stopwatch(String message) {
        this.message = message;
        this.initial = new Date();
    }

    @Override
    public void close() throws CmdException {
        Date ending = new Date();
        System.err.println(message + " -> " + (ending.getTime() - initial.getTime()) + " mseg");
    }
}
