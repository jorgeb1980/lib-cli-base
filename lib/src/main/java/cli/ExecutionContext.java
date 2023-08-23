package cli;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;

public record ExecutionContext(
    Path currentPath,
    PrintWriter standardOutput,
    PrintWriter errorOutput
) { }