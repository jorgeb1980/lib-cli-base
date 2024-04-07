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

    @Parameter(name="param1", longName="parameter1", description="first parameter", hasArg=true)
    String param1;
    public void setParam1(String p) { param1 = p; }

    @Parameter(name="param2", longName="parameter2", description="second parameter", hasArg=true)
    String param2;
    public void setParam2(String p) { param2 = p; }

    @Parameter(name="flag", longName="booleanflag", description="true/false")
    Boolean flag = false;
    public void setFlag(Boolean f) { flag = f; }

    @Run
    // Entry point for df
    public int execute(ExecutionContext ctx) throws Exception {
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
- Every command class must define a method annotated with `cli.annotations.Run` and receiving a single parameter of type 
`cli.ExecutionContext` - this is your entry point for the command
- Every command class may define optionally fields annotated with `cli.annotations.Parameter`, that will be translated into 
parameters for the script.
  - Each one allows to define `name`, `longName`, `description`, `hasArg` and `mandatory` attributes
    - `name`, `longName` will be used to identify the parameters in the command line.  At least one must be defined.
      - `name` = "f": means the script can be called with `-f` or `--f`
      - `longName` = "file": means the script can be called with `-file` or `--file`
        - If both are present, all 4 alternatives are valid
    - `description` will be used to generate the `--help` output
    - `hasArg`: (default: false) if false, the parameter is a flag and should be declared as `Boolean` in the command class.  If true, it 
is expected to have some value and should be of type `String`
    - `mandatory`: if true, the framework will enforce its presence

### Output

The generated shell scripts will just output whatever is written into 
`ExecutionContext::standardOutput` and `ExecutionContext::errorOutput`; however, if the env variable `CLI_LOG_LEVEL` is
set to one of the predefined values in `java.util.logging.Level`, it will override the behavior and log additional info
if asked.