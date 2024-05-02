package cli;

import cli.annotations.Command;
import cli.annotations.OptionalArgs;
import cli.annotations.Parameter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Common launcher for all the commands. 
 */
class EntryPoint {

	// Logger
	private final static Logger logger = Logger.getLogger(EntryPoint.class.getName());

	/**
	 * Entry point for the command launcher.
	 * @param args Command arguments
	 */
	public static void main(String[] args) {
		int ret = 0;
		try (var s = new Stopwatch("Total time")) {
			var currentPath = Paths.get("").toAbsolutePath();
			var command = head(args);
			var rest = tail(args);
			// Special case: when asked for --help, print the help straight now
			if (rest.length == 1 && Arrays.stream(rest).toList().contains("--help")) {
				printHelp(command);
			} else {
				var entry = new EntryPoint();
				ret = entry.executeEntryPoint(command, currentPath, rest);
			}
		} catch (CmdException cmde) {
			logger.log(Level.SEVERE, cmde.getMessage(), cmde);
			System.exit(cmde.getReturnCode());
		}
		// The command returned some exit code, this is our return code
		System.exit(ret);
	}

	private static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
	// Prints the help generated by Apache Commons Cli for the specified
	//	command
	private static void printHelp(String commandClassName) throws CmdException {
		var introspection = new Introspection(commandClassName);
		var commandClass = introspection.getCommandClass();
		var options = buildOptions(commandClass);
		var optionalArg = lookForOptionalArgs(commandClass);
		var commandName = commandClass.getAnnotation(Command.class).command();
		var commandDescription = commandClass.getAnnotation(Command.class).description();
		var footer = "%nv%s";

		var formatter = new HelpFormatter();
		if (optionalArg != null) {
			// Format the usage in order to show a list of arguments if necessary
			var baos = new ByteArrayOutputStream();
			var writer = new OutputStreamWriter(baos, UTF_8);
			var pwTemp = new PrintWriter(writer);
			formatter.printUsage(pwTemp, 120, "ls", options);
			pwTemp.flush();
            var usage = (baos.toString(UTF_8) + format(" [%s]...", optionalArg)).replaceAll("[\r\n]", "");
			usage = usage.replace("usage:", "");
			formatter.printHelp(
				usage, 
				commandDescription, 
				options,
				isEmpty(getVersion()) ? null : format(footer, getVersion()),
				false);
		}
		else {
			formatter.printHelp(
				commandName, 
				commandDescription, 
				options,
				isEmpty(getVersion()) ? null : format(footer, getVersion()),
				true);
		}
	}

	// Looks for an OptionalArgs annotation; it means that the command accepts
	//	additional arguments such as ls or df.
	// It returns the name argument of the OptionalArgs annotation or null if
	//	not found
	private static String lookForOptionalArgs(@SuppressWarnings("rawtypes") Class commandClass) {
		var fields = commandClass.getDeclaredFields();
		String ret = null;
		for (var field: fields) {
			if (field.isAnnotationPresent(OptionalArgs.class)) {
				var annotation = field.getAnnotation(OptionalArgs.class);
				ret = annotation.name();
			}
		}
		return ret;
	}

	// Gets the current application version from the cli.properties file
	private static String getVersion() {
		var ret = "";
		try (var is = EntryPoint.class.getClassLoader().getResourceAsStream("cli.properties")) {
			var prop = new Properties();
			prop.load(is);
			ret = prop.getProperty("cli.version");
		}
		catch(Exception e) {
			// No version
			logger.log(Level.FINE, "Could not find cli.version in cli.properties - Please define it in order to get a version footer in the command help");
		}
		return ret;
	}

	// Initializes an entry point with its proper standard and error output
	//	buffers
	public EntryPoint() { }

	/**
	 * Executes a command with certain parameters and path.
	 * @param command Name of the command to execute.
	 * @param commandArguments Arguments for the command.
	 * @param currentPath File path where the command is executed.
	 * @throws CmdException if any error is reached during the execution.
	 */
	public int executeEntryPoint(String command, Path currentPath, String... commandArguments)
			throws CmdException {
		final int ret;
		if (command != null) {
			var introspection = new Introspection(command);
			try (var s = new Stopwatch("execute command")) {
				ret = executeCommand(
					introspection.getCommand(),
					introspection.getMethod(),
					currentPath,
					commandArguments
				);
			}
		}
		else {
			throw new CmdException("Please specify which command you wish to launch", -1);
		}
		return ret;
	}
	
	// Executes the line command, returning the exit code
	private Integer executeCommand(
			Object command, 
			Method execute, 
			Path currentPath,
			String... args) throws CmdException {
		Object ret;
		try {
			// Every command should have an execute method with:
			// + Current path for the command
			var commandLine = new DefaultParser().parse(
				buildOptions(command.getClass()), 
				args,
				// Fail if there is something unrecognized
				false
			);
			applyArguments(command, commandLine);
			// Apply the command line to the command
			ret = execute.invoke(command, currentPath);
		} 
		catch (IllegalAccessException 
				| IllegalArgumentException 
				| InvocationTargetException
				| ParseException e) {
			if (e instanceof MissingOptionException) {
				ret = -1;
				printHelp(command.getClass().getName());
			} else
				throw new CmdException(e).setReturnCode(-1337);
		}
		return (Integer) ret;
	}
	
	// This method applies every argument in the command line to the command object
	private void applyArguments(Object command, CommandLine commandLine) 
			throws IllegalAccessException, InvocationTargetException, ParseException {
		// Map the setter methods to commandLine option names
		var methods = command.getClass().getMethods();
		var fields = command.getClass().getDeclaredFields();
		var methodsMap = new HashMap<String, Method>();
		Method optionalArgsMethod = null;
		for (var field: fields) {
			var method = lookForSetter(field, methods);
			if (field.isAnnotationPresent(Parameter.class)) {
				var parameter = field.getAnnotation(Parameter.class);
				if (!parameter.name().trim().isEmpty()) {
					methodsMap.put(parameter.name(), method);
				}
				if (!parameter.longName().trim().isEmpty()) {
					methodsMap.put(parameter.longName(), method);
				}
				// Force false for all flags before applying the arguments - we assume it is turned down!
				if (isBooleanType(field)) {
					method.invoke(command, Boolean.FALSE);
				}
			}
			else if (method != null && field.isAnnotationPresent(OptionalArgs.class)) {
				optionalArgsMethod = method;
			}
		}
		// Apply the command line values to the indexed methods
		// Every method is supposed to be a simple setter with one argument, of
		//		one of the following types :
		//	String
		//	Integer
		//	Long
		//	Double
		//	Float
		// An argument-less parameter is supposed to be a Boolean, and if it is
		//	present, it is assumed to be true

		var errors = new LinkedList<Exception>();
		for (var option: commandLine.getOptions()) {
			var method = methodsMap.get(option.getOpt());
			if (method == null) {
				method = methodsMap.get(option.getLongOpt());
			}
			if (method != null) {
				try {
					method.invoke(command, processArgument(method, option.getValue()));
				} catch (Exception e) {
					errors.add(e);
				}
			}
		}
		if (!errors.isEmpty()) {
			// Unable to parse certain options
			throw new ParseException(errors.stream().map(Exception::getMessage).collect(Collectors.joining("\n")));
		}
		if (optionalArgsMethod != null) {
			// The method must exist and may receive a list of strings as its only parameter
			var args = Arrays.asList(commandLine.getArgs());
			if (!args.isEmpty()) {
				optionalArgsMethod.invoke(command, args);
			}
		} else {
			if (commandLine.getArgs() != null && commandLine.getArgs().length > 0) {
				// The command line parsed args and it is not contemplated
				throw new ParseException(
					String.format(
						"Could not parse %s %n",
                        String.join(",", commandLine.getArgs())
					)
				);
			}
		}
	}

	// Gets a string, sets all the string to lower case, then the first
	//	character to upper case.
	private String firstInUpperCase(String fieldName) {
		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}
	
	// Looks for the setter method of a class field
	private Method lookForSetter(Field field, Method[] methods) {
		Method ret = null;
		if (field != null && methods != null && methods.length > 0) {
			int i = 0;
			String expectedMethodName = "set" + firstInUpperCase(field.getName());
			while (ret == null && i < methods.length) {
				Method method = methods[i++];
				if (method.getName().equals(expectedMethodName)) {
					ret = method;
				}
			}
		}
		return ret;
	}

	private Object getEnum(Class<?> clazz, String value) throws ParseException {
		if (value == null) throw new ParseException(String.format("Could not parse an empty value to type %s", clazz.getName()));
		try {
			var valueOf = clazz.getMethod("valueOf", String.class);
			return valueOf.invoke(null, value.toUpperCase());
		} catch (Exception e) {
			throw new ParseException(String.format("Could not parse %s to type %s", value, clazz.getName()));
		}
	}

	// This method processes the option value in order to pass the right type
	//	to the command object
	private Object processArgument(Method method, String value) throws ParseException {
		Object ret = null;
		Class<?>[] types = method.getParameterTypes();
		try {
			if (types.length == 1) {
				Class<?> type = types[0];
				if (type.isEnum()) {
					ret = getEnum(type, value);
				} else if (type.equals(String.class)) {
					ret = value;
				} else if (type.equals(Integer.class)) {
					ret = Integer.valueOf(value);
				} else if (type.equals(Long.class)) {
					ret = Long.valueOf(value);
				}
				// This can be improved by looking closely at precision, etc.
				else if (type.equals(Double.class)) {
					ret = Double.valueOf(value);
				} else if (type.equals(Float.class)) {
					ret = Float.valueOf(value);
				}
				// Boolean arguments
				else if (type.equals(Boolean.class)) {
					if (value != null) { throw new ParseException("Boolean types do not allow a value"); }
					else {
						// Now we activate the boolean flag we had previously turned down
						ret = Boolean.TRUE;
					}
				}
			}
		} catch (NumberFormatException nfe) {
			throw new ParseException(String.format("Could not parse '%s' to the expected type", value));
		}
		return ret;
	}

	// We assume that every non-boolean type will require arguments
	private static boolean isBooleanType(Field f) {
		return f.getType().equals(boolean.class) || f.getType().equals(Boolean.class);
	}

	// This method build an Apache Command Line Options object upon
	//	the annotated parameters information in the class
	public static Options buildOptions(Class<?> commandClass) {
		// It will be the fields that get annotated, and those fields will get
		//	us to the setter method
		var fields = commandClass.getDeclaredFields();
		var options = new Options();
		for (var field: fields) {
			if (field.isAnnotationPresent(Parameter.class)) {
				var parameter = field.getAnnotation(Parameter.class);
				// Build the option
				var optionBuilder = Option.builder().
					required(parameter.mandatory()).
					hasArg(!isBooleanType(field)).
					desc(parameter.description());
				if (!parameter.longName().isEmpty())
					optionBuilder.longOpt(parameter.longName());
				if (!parameter.name().trim().isEmpty())
					optionBuilder.option(parameter.name());
				options.addOption(optionBuilder.build());
			}
		}
		return options;
	}

	/** Returns the first argument */
	public static String head(String[] args) {
		return args != null && args.length > 0 ? args[0] : null;
	}
	
	/** Returns the second and later arguments*/
	public static String[] tail(String[] args) {
		return Arrays.copyOfRange(args, 1, args.length);
	}
}
