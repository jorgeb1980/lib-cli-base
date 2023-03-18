package cli;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * This class allows to print a long integer in a human readable format.
 */
public class HumanReadableFormat {
	
	//--------------------------------------------------------------------
	// Class constants
	
	// Kilobytes
	private static final int UNIT = 1024;
	// Decimal point format
	private static final Locale LOCALE = new Locale("es");
	
	//--------------------------------------------------------------------
	// Class methods
	
	/**
	 * This method formats a number into a human readable format. 
	 * @param number Long number.
	 * @return The number turned into a human readable size (e.g., 1K 234M 2G)
	 */
	public static String format(long number) {
		return format(new BigDecimal(number));
	}
	
	/**
	 * This method formats a number into a human readable format. 
	 * @param number Long floating point number.
	 * @return The number turned into a human readable size (e.g., 1K 234M 2G)
	 */
	public static String format(double number) {
		return format(new BigDecimal(number));
	}
	
	/**
	 * This method formats a number into a human readable format. 
	 * @param number Long big decimal number.
	 * @return The number turned into a human readable size (e.g., 1K 234M 2G)
	 */
	public static String format(BigDecimal bigDecimal) {
		long bytes = bigDecimal.toBigInteger().longValue();
		if (bigDecimal.compareTo(new BigDecimal(UNIT)) < 0) return bigDecimal.toString();
		int exp = (int) (Math.log(bytes) / Math.log(UNIT));
	    String pre = Character.toString("KMGTPE".charAt(exp-1));
	    return String.format(LOCALE, "%.1f%s", bytes / Math.pow(UNIT, exp), pre);
	}
}
