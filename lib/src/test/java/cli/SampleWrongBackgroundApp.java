package cli;

import cli.annotations.Command;
import cli.annotations.Run;

import java.nio.file.Path;

@Command(command = "background", isBackground = true, description = "Runs in the background")
public class SampleWrongBackgroundApp {

    @Run
    public int execute(Path cwd) {
        System.out.println(cwd);
        // This should not fly! returning int instead of null
        return -1235235;
    }
}
