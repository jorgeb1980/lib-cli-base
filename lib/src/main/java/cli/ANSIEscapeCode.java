package cli;

public enum ANSIEscapeCode {

	
	CLEAR(0),
	BLACK(30),
	RED(31),
	GREEN(32),
	YELLOW(33),
	BLUE(34),
	MAGENTA(35),
	CYAN(36),
	WHITE(37),
	BRIGHT_BLUE(34, true),
	BRIGHT_GREEN(32, true);
	
	private final int code;
	private boolean bright = false;

	/**
	 * Builds an escape code object
	 * @param code Internal code representation
	 */
	private ANSIEscapeCode(int code) {
		this.code = code;
	}

	private ANSIEscapeCode(int code, Boolean bright) {
		this(code);
		this.bright = bright;
	}
	
	/**
	 * ANSI escape code for this color
	 * @return Internal code representation
	 */
	private String getCode() {
		return Integer.toString(this.code);
	}
	private Boolean isBright() { return this.bright; }
	
	/**
	 * Returns a string 'painted' in the specified color.
	 * @param s String to paint.
	 * @param code Color code to paint the string with.
	 * @return Painted string
	 */
	public static String paint(String s, ANSIEscapeCode code) {
		return (char)27 + "[" + code.getCode() + (code.isBright() ? ";1" : "") + "m" + s + (char)27 + "[0m";
	}
}
