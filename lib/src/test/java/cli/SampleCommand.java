package cli;

import cli.annotations.Command;
import cli.annotations.Parameter;
import cli.annotations.Run;

import java.nio.file.Path;

@Command(command = "test", description = "this is a test command")
public class SampleCommand {

    @Parameter(
        name = "testParam",
        description = "test parameter"
    )
    private String testParam;
    public void setTestParam(String t) { testParam = t; }

    @Parameter(
        name = "enumParam",
        description = "enumerated parameter"
    )
    private SampleEnum enumParam;
    public void setEnumParam(SampleEnum e) { enumParam = e; }

    @Parameter(
        name = "numericParam",
        description = "numeric parameter"
    )
    private Long numericParam;
    public void setNumericParam(Long l) { numericParam = l; }

    @Parameter(
        name = "flag",
        description = "true if set"
    )
    private Boolean booleanParam;
    public void setBooleanParam(Boolean b) { booleanParam = b; }

    @Run
    public int someMethod(Path cwd) {
        System.out.println(cwd);
        System.out.println(testParam);
        System.out.println(enumParam);
        System.out.println(numericParam);
        System.out.println(booleanParam);
        return 0;
    }
}
