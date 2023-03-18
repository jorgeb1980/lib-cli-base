package cli;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class TestIntrospection {

    @Test
    public void testSampleCommandIntrospection() {
        try {
            var introspection = new Introspection("cli.SampleCommand");
            assertNotNull(introspection.getCommand());
            assertNotNull(introspection.getMethod());
            assertEquals("cli.SampleCommand", introspection.getCommand().getClass().getName());
            Method m = introspection.getMethod();
            assertEquals("someMethod", m.getName());
        } catch (CmdException e) {
            fail(e);
        }
    }
}
