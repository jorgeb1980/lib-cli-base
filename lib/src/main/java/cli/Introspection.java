package cli;

import cli.annotations.Run;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class Introspection {

    private final String commandClassName;
    private Class<?> commandClass;
    private Object command;
    private Method method;

    public Introspection(String commandClassName) throws CmdException {
        this.commandClassName = commandClassName;
        try (var s = new Stopwatch("looking for command")) {
            lookForCommand();
        }
        // Instantiate the proper command
        try (var s = new Stopwatch("instantiate command")) {
            instantiateCommand();
        }
        // Look for an 'execute' method with the next arguments:
        try (var s = new Stopwatch("find execute method")) {
            findExecuteMethod();
        }
    }

    public Method getMethod() { return method; }
    public Object getCommand() { return command; }
    public Class<?> getCommandClass() { return commandClass; }

    // Looks for the proper command class
    // The search with google reflections has been optimized away to the build process
    //	in order to get an improvement of ~0.15 sec for each program call.
    @SuppressWarnings("rawtypes")
    private void lookForCommand()
        throws CmdException {
        try {
            commandClass = Class.forName(commandClassName);
        }
        catch(Exception e) {
            throw new CmdException(e);
        }
    }

    // Instantiates a command object
    private void instantiateCommand() throws CmdException {
        try {
            command = commandClass.getConstructor().newInstance();
        }
        catch (InstantiationException
               | IllegalAccessException
               | IllegalArgumentException
               | InvocationTargetException
               | NoSuchMethodException
               | SecurityException e) {
            throw new CmdException(e).setReturnCode(-1337);
        }
    }

    // Finds an execute method in the command class
    private void findExecuteMethod() throws CmdException {
        try {
            var methods =
                Arrays.stream(commandClass.getDeclaredMethods()).filter(
                    m -> m.isAnnotationPresent(Run.class)
                ).toList();
            if (methods.isEmpty()) throw new NoSuchElementException();
            else if (methods.size() > 1) throw new CmdException("Only one method annotated as 'Run' per command class");
            else {
                var m = methods.getFirst();
                if (m.getParameters().length != 1 || m.getParameters()[0].getType() != Path.class)
                    throw new CmdException("The method annotated as 'Run' must have exactly one argument of the class 'ExecutionContext'");
                else method = m;
            }
        } catch (NoSuchElementException | SecurityException e) {
            throw new CmdException(e).setReturnCode(-1337);
        }
    }
}
