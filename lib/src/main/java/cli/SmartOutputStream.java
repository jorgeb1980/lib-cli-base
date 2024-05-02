package cli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SmartOutputStream extends OutputStream {

    private final OutputStream os;
    private final ByteArrayOutputStream baos;

    public SmartOutputStream(OutputStream os, ByteArrayOutputStream baos) {
        this.os = os;
        this.baos = baos;
    }

    @Override
    public void write(int b) throws IOException {
        os.write(b);
        baos.write(b);
    }
}
