import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleCommandTest {

    @Test
    public void testCommand() {
        SampleCommand sc = new SampleCommand();
        sc.setParam1("abcd");
        sc.setParam2("xyz");

        assertEquals("abcd", sc.param1);
        assertEquals("xyz", sc.param2);
    }
}