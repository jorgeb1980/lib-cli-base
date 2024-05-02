import cli.annotations.Command;
import cli.annotations.Parameter;
import cli.annotations.Run;

import java.nio.file.Path;

@Command(command="sample", description="Sample command")
public class SampleCommand {

    @Parameter(name="param1", longName="parameter1", description="first parameter")
    String param1;
    public void setParam1(String p) { param1 = p; }

    @Parameter(name="param2", longName="parameter2", description="second parameter")
    String param2;
    public void setParam2(String p) { param2 = p; }

    @Parameter(name="flag", longName="booleanflag", description="true if set")
    Boolean flag = false;
    public void setFlag(Boolean f) { flag = f; }

    @Run
    // Entry point for df
    public int execute(Path cwd) throws Exception {
        System.out.printf("Running on %s%n", cwd);
        System.out.printf("Parameters: %s %s %s%n", param1, param2, flag);
        return 0;
    }
}