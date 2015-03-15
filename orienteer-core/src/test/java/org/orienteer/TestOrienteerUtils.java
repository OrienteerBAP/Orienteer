package org.orienteer;

import org.junit.Test;
import org.orienteer.CustomAttributes;

import static org.junit.Assert.*;

public class TestOrienteerUtils
{
	@Test
	public void testCustomEncode() throws Exception
	{
		assertNull(CustomAttributes.encodeCustomValue(null));
		assertEquals("", CustomAttributes.encodeCustomValue(""));
		assertEquals("test", CustomAttributes.encodeCustomValue("test"));
		assertEquals("test\\etest", CustomAttributes.encodeCustomValue("test=test"));
		assertEquals("test\\rtest", CustomAttributes.encodeCustomValue("test\rtest"));
		assertEquals("test\\ntest", CustomAttributes.encodeCustomValue("test\ntest"));
		assertEquals("test\\\\test", CustomAttributes.encodeCustomValue("test\\test"));
		assertEquals("test\\etest\\rtest\\ntest\\\\test", CustomAttributes.encodeCustomValue("test=test\rtest\ntest\\test"));
	}
	
	@Test
	public void testCustomDecode() throws Exception
	{
		assertNull(CustomAttributes.decodeCustomValue(null));
		assertEquals("", CustomAttributes.decodeCustomValue(""));
		assertEquals("test", CustomAttributes.decodeCustomValue("test"));
		assertEquals("test=test", CustomAttributes.decodeCustomValue("test\\etest"));
		assertEquals("test\rtest", CustomAttributes.decodeCustomValue("test\\rtest"));
		assertEquals("test\ntest", CustomAttributes.decodeCustomValue("test\\ntest"));
		assertEquals("test\\test", CustomAttributes.decodeCustomValue("test\\\\test"));
		assertEquals("test=test\rtest\ntest\\test", CustomAttributes.decodeCustomValue("test\\etest\\rtest\\ntest\\\\test"));
	}
	
}
