import cli.ExecutionContext;
import cli.annotations.Command;
import cli.annotations.Parameter;
import cli.annotations.Run;

@Command(command="sample", description="Sample command")
public class SampleCommand {

    @Parameter(name="param1", longName="parameter1", description="first parameter")
    String param1;

    public void setParam1(String p) { param1 = p; }

    @Parameter(name="param2", longName="parameter2", description="second parameter")
    String param2;

    public void setParam2(String p) { param2 = p; }

    @Run
    // Entry point for df
    public int execute(ExecutionContext ctx) throws Exception {
        System.out.printf("Parameters: %s %s%n", param1, param2);
        return 0;
    }
}