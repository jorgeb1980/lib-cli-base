package cli;

import cli.annotations.Command;
import cli.annotations.Parameter;
import cli.annotations.Run;

@Command(command = "test", description = "this is a test command")
public class SampleCommand {

    @Parameter(
        name = "testParam",
        description = "test parameter"
    )
    private String testParam;

    @Run
    public int someMethod(ExecutionContext ctx) {
        return -1;
    }
}
