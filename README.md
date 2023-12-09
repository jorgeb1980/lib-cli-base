# lib-cli-base
Simple framework for creating command-line apps in java.

## TL;DR
This project contains a maven plugin and a library.  Any java project may include the library in its dependencies and the 
plugin in its maven pom.xml - this allows the project to create annotated POJOs that the plugin will wrap with a templated 
shell script.

## How to use this framework

Include the dependency for the lib and the plugin itself in your pom.xml

### Build file

```maven
<properties>
    <cli.library.version>X.Y</cli.library.version>
</properties>
<plugin>
  <groupId>cli</groupId>
  <artifactId>files-generator-maven-plugin</artifactId>
  <version>${cli.library.version}</version>
  <configuration>
      <packageName>whatever.package.will.contain.your.annotated.pojos</packageName>
  </configuration>
  <executions>
      <execution>
          <goals>
              <goal>generate-files</goal>
          </goals>
      </execution>
  </executions>
</plugin>
...
<dependencies>
  <dependency>
      <groupId>cli</groupId>
      <artifactId>lib-cli</artifactId>
      <version>${cli.library.version}</version>
  </dependency>
  ...
</dependencies>
```

Including this will generate the following structure:

```
├── target
│   ├── redist
│   │   ├── scripts
│   │   ├── libs
```

It is up to the user to make it sure that whatever is necessary lands into libs - e.g., by using `maven-dependency-plugin` target 
`copy-dependencies`, like this:

```maven
<plugin>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <execution>
            <phase>install</phase>
            <goals>
                <goal>copy-dependencies</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.directory}/redist/libs</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Code

Create annotated command classes following these rules:
- Remember your `packageName` - this is rather a performance decision in order not to scan the whole classpath 
in build time
- Every command class must be annotated with `cli.annotations.Command`
  - The `command` attribute will be translated to the script name
  - The `description` attribute will be used to generate the `--help` output
- Every command class must define a method annotated with `cli.annotations.Run` and receiving a single parameter of type 
`cli.ExecutionContext` - this is your entry point for the command
- Every command class may define optionally fields annotated with `cli.annotations.Parameter`, that will be translated into 
parameters for the script.
  - Each one allows to define `name`, `longName`, `description`, `hasArg` and `mandatory` attributes
    - `name`, `longName` will be used to identify the parameters in the command line.  At least one must be defined.
      - `name` = "f": means the script can be called with `-f`
      - `longName` = "file": means the script can be called with `--file`  
    - `description` will be used to generate the `--help` output
    - `hasArg`: if false, the parameter is a flag and should be declared as `Boolean` in the command class.  If true, it 
is expected to have some value and should be of type `String`
    - `mandatory`: if true, the framework will enforce its presence

For example, this class:

```java
import cli.ExecutionContext;
import cli.annotations.Command;
import cli.annotations.Parameter;
import cli.annotations.Run;
import lombok.Setter;

@Command(command = "whatever", description="sample command")
public SampleCommand() {
    
    @Setter // Remember that the framework will need to set this at some moment!
    @Parameter(name="f", longName="file", mandatory=true, hasArg=true, description="some file")
    private String file;
    @Setter
    @Parameter(name="v", longName="verbose", description="triggers chatty behavior")
    private Boolean verbose;

    @Run
    public int execute(ExecutionContext ctx) throws Exception {
        // do something
    }
}
```

Would generate in `target/redist/scripts` 2 files called `whatever.sh` and `whatever.bat` that rely on finding everything 
necessary into  `target/redist/libs`.  If that is taken care of, the use can safely release the contents of  `target/redist`.

Every annotated class will generate its own script in `target/redist/scripts`.