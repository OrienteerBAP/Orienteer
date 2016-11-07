package org.orienteer.core;

import org.junit.Test;
import org.orienteer.core.CustomAttribute;

import static org.junit.Assert.*;

public class OrienteerUtilsTest
{
	@Test
	public void testCustomEncode() throws Exception
	{
		assertNull(CustomAttribute.encodeCustomValue(null));
		assertEquals("", CustomAttribute.encodeCustomValue(""));
		assertEquals("test", CustomAttribute.encodeCustomValue("test"));
		assertEquals("test\\etest", CustomAttribute.encodeCustomValue("test=test"));
		assertEquals("test\\rtest", CustomAttribute.encodeCustomValue("test\rtest"));
		assertEquals("test\\ntest", CustomAttribute.encodeCustomValue("test\ntest"));
		assertEquals("test\\\\test", CustomAttribute.encodeCustomValue("test\\test"));
		assertEquals("test\\etest\\rtest\\ntest\\\\test", CustomAttribute.encodeCustomValue("test=test\rtest\ntest\\test"));
	}
	
	@Test
	public void testCustomDecode() throws Exception
	{
		assertNull(CustomAttribute.decodeCustomValue(null));
		assertEquals("", CustomAttribute.decodeCustomValue(""));
		assertEquals("test", CustomAttribute.decodeCustomValue("test"));
		assertEquals("test=test", CustomAttribute.decodeCustomValue("test\\etest"));
		assertEquals("test\rtest", CustomAttribute.decodeCustomValue("test\\rtest"));
		assertEquals("test\ntest", CustomAttribute.decodeCustomValue("test\\ntest"));
		assertEquals("test\\test", CustomAttribute.decodeCustomValue("test\\\\test"));
		assertEquals("test=test\rtest\ntest\\test", CustomAttribute.decodeCustomValue("test\\etest\\rtest\\ntest\\\\test"));
	}
	
}
