import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SampleCommandTest {

    @Test
    public void testCommand() {
        SampleCommand sc = new SampleCommand();
        sc.setParam1("abcd");
        sc.setParam2("xyz");
        sc.setFlag(true);

        assertEquals("abcd", sc.param1);
        assertEquals("xyz", sc.param2);
        assertTrue(sc.flag);
    }
}