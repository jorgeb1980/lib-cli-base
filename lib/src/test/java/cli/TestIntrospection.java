package cli;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Paths;

import static java.util.Arrays.asList;
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

    @Test
    public void testEntryPoint() {
        try {
            var result = new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                asList("-testParam", "lalala", "-enumParam", "FOO", "-numericParam", "234", "-flag"),
                Paths.get("")
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
                asList("-enumParam", "bar"),
                Paths.get("")
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
                asList("-testParam"),
                Paths.get("")
            );
        });
    }

    @Test
    public void testArgMandatoryNumeric() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                asList("-testNumeric"),
                Paths.get("")
            );
        });
    }

    @Test
    public void testArgMandatoryEnum() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                asList("-testEnum"),
                Paths.get("")
            );
        });
    }

    @Test
    public void testBooleanShouldNotHaveArguments() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                asList("-flag", "-enumParam", "bar"),
                Paths.get("")
            );
        });
    }

    @Test
    public void testEntryPointWrongEnum() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                asList("-enumParam", "doesNotExistInEnumType"),
                Paths.get("")
            );
        });
    }

    @Test
    public void testEntryPointWrongNumeric() {
        assertThrowsExactly(CmdException.class, () -> {
            new EntryPoint().executeEntryPoint(
                "cli.SampleCommand",
                asList("-numericParam", "thisIsNotANumber"),
                Paths.get("")
            );
        });
    }
}
