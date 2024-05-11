package cli.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a command line action. 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {	
	// Name of the command
	String command();
	// Command description
	String description();
	// Additional args necessary for java vm
	String jvmArgs() default "";
	// Is the command intended to be a background app? like a swing app
	boolean isBackground() default false;
}
