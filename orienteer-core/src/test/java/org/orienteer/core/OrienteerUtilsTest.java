package org.orienteer.core;

import org.junit.Test;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.util.CommonUtils;

import com.orientechnologies.orient.core.metadata.schema.OType;

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
	
	@Test
	public void testDiffAnnotations() throws Exception {
		DAOField daoField1 = OrienteerUtilsTest.class.getDeclaredMethod("dummyMethod1").getAnnotation(DAOField.class);
		DAOField daoField2 = OrienteerUtilsTest.class.getDeclaredMethod("dummyMethod2").getAnnotation(DAOField.class);
		assertArrayEquals(new Object[] {"value"}, CommonUtils.diffAnnotations(daoField1, daoField2).toArray());
		System.out.println(CommonUtils.diffAnnotations(daoField1, DAO.dao(DAOField.class)));
		assertArrayEquals(new Object[] {"type", "value"}, CommonUtils.diffAnnotations(daoField1, DAO.dao(DAOField.class)).toArray());
	}
	
	@DAOField(value = "method1", type = OType.INTEGER)
	public void dummyMethod1() {
		
	}
	
	@DAOField(value = "method2", type = OType.INTEGER)
	public void dummyMethod2() {
		
	}
	
}
