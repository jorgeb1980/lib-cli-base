package cli.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a parameter for a command line call.  It indicates an option 
 * for the command call.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
	// Name of the parameter
	String name() default "";
	// Optional long name
	String longName() default "";
	// Description
	String description();
	// Is it mandatory?
	boolean mandatory() default false;
}
