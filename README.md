# lib-cli-base
Simple framework for creating command-line apps in java.

## TL;DR
This framework allows to create command line tools with annotated java classes and (hopefully) the minimum overhead.

The project contains a maven archetype, along with a plugin and a library.  Any java project may include the library in its dependencies and the 
plugin in its maven pom.xml - this allows the project to create annotated POJOs that the plugin will wrap with a templated 
shell script.

## How to use this framework

The suggested approach is to use the maven archetype that creates a minimal project with a sample command.  Just run 
```
mvn archetype:generate -DarchetypeGroupId=cli-library \
                       -DarchetypeArtifactId=cli-archetype \
                       -DarchetypeVersion=XXX \
                       -DgroupId=my-group \
                       -DartifactId=my-artifact \
                       -Dversion=1.0-SNAPSHOT
```

This will generate a maven project with the following structure:

```
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java
│   │   │   ├── SampleCommand.java
│   ├── test/
│   │   ├── java
│   │   │   ├── SampleCommandTest.java
```

`SampleCommand` is an example of a class annotated to become a shell script with 3 simple parameters.

```java
@Command(command="sample", description="Sample command")
public class SampleCommand {

    @Parameter(name="param1", longName="parameter1", description="first parameter")
    String param1;
    public void setParam1(String p) { param1 = p; }

    @Parameter(name="param2", longName="parameter2", description="second parameter")
    String param2;
    public void setParam2(String p) { param2 = p; }

    @Parameter(name="flag", longName="booleanflag", description="true/false")
    Boolean flag = false;
    public void setFlag(Boolean f) { flag = f; }

    @Run
    // Entry point for df
    public int execute(Path cwd) throws Exception {
        System.out.printf("Parameters: %s %s %s%n", param1, param2, flag);
        return 0;
    }
}
```

Runnin `mvn install` in the created project will generate the associated `redist/` directory with the necessary 
scripts and libraries to be deployed in a windows or unix-like machine.

```
├── target/
│   ├── redist/
│   │   ├── scripts/
│   │   │   ├── sample.sh
│   │   │   ├── sample.bat
│   │   ├── libs/
│   │   │   ├── my-artifact-1.0-SNAPSHOT.jar
│   │   │   ├── cli-command-library-XXX.jar
│   │   │   ├── ...
```
The `libs/` directory will contain whatever runtime dependencies are defined for the project, as well 
as the result of compiling the project itself.

Running `sample.sh --help` (or in windows machines, `sample.bat --help`) will result in
```
usage: sample [-flag] [-param1 <arg>] [-param2 <arg>]
Sample command
 -flag,--booleanflag          true/false
 -param1,--parameter1 <arg>   first parameter
 -param2,--parameter2 <arg>   second parameter
```

### Code

Create annotated command classes following these rules:
- Every command class must be annotated with `cli.annotations.Command`
  - The `command` attribute will be translated to the script name
  - The `description` attribute will be used to generate the `--help` output
  - The optional `jvmArgs` attribute will be added to the java virtual machine command line - use it for additional heap, etc.
  - The optional `isBackgroundApp` attribute will allow to create Swing UIs or similar by telling the framework not to exit immediately.
- Every command class must define a method annotated with `cli.annotations.Run` and receiving a single parameter of type 
`java.nio.file.Path` with the working directory
- Every command class may define optionally fields annotated with `cli.annotations.Parameter`, that will be translated into 
parameters for the script.
  - Each one allows to define `name`, `longName`, `description` and `mandatory` attributes
    - `name`, `longName` will be used to identify the parameters in the command line.  At least one must be defined.
      - `name` = "f": means the script can be called with `-f` or `--f`
      - `longName` = "file": means the script can be called with `-file` or `--file`
        - If both are present, all 4 alternatives are valid
    - `description` will be used to generate the `--help` output
    - `mandatory`: if true, the framework will enforce its presence
  - If a property is annotated with `cli.annotations.OptionalArgs`, with the type `List<String>`, it will be used to gather
whatever additional parameters are found in the command line.  Consider for example in the `ls` command, the `FILE` parameter:
    - ```usage:  ls [-a] [-A] [-B] <edited...> [-I <arg>] [-l] [-R] [FILE]...```

### Debugging

If the env variable `CLI_LOG_LEVEL` is
set to one of the predefined values in `java.util.logging.Level`, the framework will log additional info.

```
$ CLI_LOG_LEVEL=FINEST ./target/redist/scripts/ls.sh --color -hal pom.xml
[2024-05-02 17:06:45] [FINEST] Overridden log level to FINEST by env var CLI_LOG_LEVEL
[2024-05-02 17:06:46] [FINE  ] looking for command -> 12 mseg
[2024-05-02 17:06:46] [FINE  ] instantiate command -> 0 mseg
[2024-05-02 17:06:46] [FINE  ] find execute method -> 9 mseg
-rw-rw-rw-    whatever   whatever     3,2K May  2 15:43 pom.xml
[2024-05-02 17:06:46] [FINE  ] execute command -> 20 mseg
[2024-05-02 17:06:46] [FINE  ] Total time -> 46 mseg
```
