package cli;

public enum ANSIEscapeCode {

	
	CLEAR(0), BLACK(30), RED(31), GREEN(32), YELLOW(33), BLUE(34), MAGENTA(35), CYAN(36), WHITE(37);
	
	private final int code;
	
	/**
	 * Builds an escape code object
	 * @param code Internal code representation
	 */
	private ANSIEscapeCode(int code) {
		this.code = code;
	}
	
	/**
	 * ANSI escape code for this color
	 * @return Internal code representation
	 */
	private String getCode() {
		return Integer.toString(this.code);
	}
	
	/**
	 * Returns a string 'painted' in the specified color.
	 * @param s String to paint.
	 * @param code Color code to paint the string with.
	 * @return Painted string
	 */
	public static String paint(String s, ANSIEscapeCode code) {
		return (char)27 + "[" + code.getCode() + "m" + s + (char)27 + "[" + WHITE.getCode() + "m"; 
	}
}
