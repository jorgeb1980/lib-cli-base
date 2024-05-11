package cli;

import cli.annotations.Command;
import cli.annotations.Run;

import java.nio.file.Path;

@Command(command = "background", isBackground = true, description = "Runs in the background")
public class SampleBackgroundApp {

    @Run
    public void run(Path cwd) {
        System.out.println(cwd);
        // Drop something into an executor service or create swing components
    }
}
