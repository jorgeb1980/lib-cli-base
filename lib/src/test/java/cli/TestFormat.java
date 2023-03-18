package cli;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Human readable format test
 */
public class TestFormat {

	@Test
	public void testZeroFormat() {
		assertEquals("0", HumanReadableFormat.format(0));
	}
	
	@Test 
	public void testByteFormats() {
		assertEquals("1", HumanReadableFormat.format(1));
		assertEquals("10", HumanReadableFormat.format(10));
		assertEquals("100", HumanReadableFormat.format(100));
		assertEquals("999", HumanReadableFormat.format(999));
		assertEquals("1000", HumanReadableFormat.format(1000));
		assertEquals("1023", HumanReadableFormat.format(1023));
	}
	
	@Test
	public void testKbyteFormats() {
		assertEquals("1,0K", HumanReadableFormat.format(1024));
		assertEquals("1,2K", HumanReadableFormat.format(1207));
		assertEquals("95,8K", HumanReadableFormat.format(95.8 * 1024));
		assertEquals("1000,0K", HumanReadableFormat.format(1000 * 1024));
		assertEquals("1001,0K", HumanReadableFormat.format(1001 * 1024));
		assertEquals("1023,0K", HumanReadableFormat.format(1023 * 1024));
		assertEquals("1023,8K", HumanReadableFormat.format(1023.8 * 1024));
	}
	
	@Test
	public void testMegabyteFormats() {
		assertEquals("1,2M", 
				HumanReadableFormat.format(1230333));
		assertEquals("1,2M", 
				HumanReadableFormat.format(1240333));
		assertEquals("1,0M", 
				HumanReadableFormat.format(1024*1024));
		assertEquals("2,0M", 
				HumanReadableFormat.format(2*1024*1024));
		assertEquals("3,0M", 
				HumanReadableFormat.format(3*1024*1024));
		assertEquals("999,0M", 
				HumanReadableFormat.format(999*1024*1024));
		assertEquals("999,9M", 
				HumanReadableFormat.format(999.9*1024*1024));
		assertEquals("999,9M", 
				HumanReadableFormat.format(999.95*1024*1024));
		assertEquals("1000,4M", 
				HumanReadableFormat.format(1000.4*1024*1024));
		assertEquals("1023,9M", 
				HumanReadableFormat.format(1023.9*1024*1024));
	}
	
	@Test 
	public void testGigaBytesFormats() {
		assertEquals("1,2G", 
				HumanReadableFormat.format(1230333 * 1024));
		assertEquals("1,2G", 
				HumanReadableFormat.format(1240333 * 1024));
		assertEquals("1,0G", 
				HumanReadableFormat.format(1024*1024*1024));
		assertEquals("2,0G", 
				HumanReadableFormat.format(Long.parseUnsignedLong("2147483648")));
		assertEquals("3,0G", 
				HumanReadableFormat.format(Long.parseUnsignedLong("3221225472")));
		assertEquals("27,0G", 
				HumanReadableFormat.format(Long.parseUnsignedLong("28991029248")));
		BigDecimal bigDecimal = new BigDecimal("28991029248");
		bigDecimal = bigDecimal.add(new BigDecimal(100 * 1024 * 1024));
		assertEquals("27,1G", 
				HumanReadableFormat.format(bigDecimal));
		BigDecimal bigDecimal2 = new BigDecimal("28991029248");
		bigDecimal2 = bigDecimal2.add(new BigDecimal(900 * 1024 * 1024));
		assertEquals("27,9G", 
				HumanReadableFormat.format(bigDecimal2));
	}
	
}
