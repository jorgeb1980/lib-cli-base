package cli;

/**
 * This class models a generic exception type for error handling through
 * the entire package.
 */
public class CmdException extends Exception {

	//------------------------------------------------------------
	// Class constants
	
	/** Return code. */
	private int code = -1337;
	
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = -2396560778835143351L;

	//------------------------------------------------------------
	// Class methods

	/**
	 * Sets the return code for the exception.
	 * @param code Return code for the command line application.
	 * @return Reference to the exception object.
	 */
	public CmdException setReturnCode(int code) {
		this.code = code;
		return this;
	}
	
	/**
	 * @return The return code
	 */
	public int getReturnCode() {
		return code;
	}

	/**
	 * Builds an exception with an error message.
	 * @param message Error message.
	 */
	public CmdException(String message) {
		super(message);
	}
	
	/**
	 * Builds an exception with an error message.
	 * @param returnCode Return code for the command line application.
	 * @param message Error message.
	 */
	public CmdException(String message, int returnCode) {
		super(message);
		setReturnCode(returnCode);
	}

	/**
	 * Builds an exception wrapping a possible cause.
	 * @param cause Cause of the exception.
	 */
	public CmdException(Exception cause) {
		super(cause);
	}

	/**
	 * Builds an exception wrapping a possible cause.
	 * @param cause Cause of the exception.
	 */
	public CmdException(Exception cause, String message, int returnCode) {
		super(message, cause);
		setReturnCode(returnCode);
	}
}
