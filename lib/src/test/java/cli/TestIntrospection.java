package cli;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.fail;

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

    @Test
    public void testEntryPoint() {
        try {
            var result = new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                Paths.get(""),
                "-testParam", "lalala", "-enumParam", "FOO", "-numericParam", "234", "-flag"
            );
            assertEquals(0, result);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testLowerCaseEnum() {
        try {
            var result = new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                Paths.get(""),
                "-enumParam", "bar"
            );
            assertEquals(0, result);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testArgMandatoryString() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                Paths.get(""),
                "-testParam"
            );
        });
    }

    @Test
    public void testArgMandatoryNumeric() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                Paths.get(""),
                "-testNumeric"
            );
        });
    }

    @Test
    public void testArgMandatoryEnum() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                Paths.get(""),
                "-testEnum"
            );
        });
    }

    @Test
    public void testBooleanShouldNotHaveArguments1() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                Paths.get(""),
                // careful - this would succeed if the sample command had optional arguments, lalala would then be
                //  interpreted as the optional args
                "-flag", "lalala", "-enumParam", "bar"
            );
        });
    }

    @Test
    public void testBooleanShouldNotHaveArguments2() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                Paths.get(""),
                "-flag", "true"
            );
        });
    }

    @Test
    public void testEntryPointWrongEnum() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                Paths.get(""),
                "-enumParam", "doesNotExistInEnumType"
            );
        });
    }

    @Test
    public void testEntryPointWrongNumeric() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                Paths.get(""),
                "-numericParam", "thisIsNotANumber"
            );
        });
    }

    @Test
    public void testBackgroundApp() {
        try {
            var result = new EntryPoint().executeEntryPoint(
                "cli.SampleBackgroundApp",
                Paths.get("")
            );
            assertNull(result);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testWrongBackgroundApp() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleWrongBackgroundApp",
                Paths.get("")
            );
        });
    }
}
