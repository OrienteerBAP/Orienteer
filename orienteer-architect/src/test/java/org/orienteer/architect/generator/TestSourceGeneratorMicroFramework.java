package org.orienteer.architect.generator;

import org.junit.Test;
import org.orienteer.architect.service.generator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class TestSourceGeneratorMicroFramework {

    private static final Logger LOG = LoggerFactory.getLogger(TestSourceGeneratorMicroFramework.class);

    @Test
    public void testNewInstance() {
        OSourceNewInstance newInteger = new OSourceNewInstance("Integer", singletonList("1"));
        assertEquals("new Integer(1)", newInteger.toJavaSrc());

        OSourceNewInstance newStringBuilder = new OSourceNewInstance("StringBuilder", singletonList("\"Test\""));
        assertEquals("new StringBuilder(\"Test\")", newStringBuilder.toJavaSrc());
    }

    @Test
    public void testStaticNewInstance() {
        ISource bindSchema = new OSourceStaticNewInstance("OSchemaHelper", "bind", singletonList("db"));
        assertEquals("OSchemaHelper.bind(db)", bindSchema.toJavaSrc());
    }

    @Test
    public void testCall() {
        ISource callTest = new OSourceCall("instance", "test", asList("1", "2", "3"));
        assertEquals("instance.test(1, 2, 3)", callTest.toJavaSrc());
    }

    @Test
    public void testChainCall() {
        ISource callChainTest = new OSourceChainCall("test", asList("1", "2", "3"));
        assertEquals("    .test(1, 2, 3)", callChainTest.toJavaSrc());
    }

    @Test
    public void testCreateNewInstanceAndCallMethods() {
        OSourceFragment fragment = new OSourceFragment();
        fragment.addSource(new OSourceVariableDeclaration("OSchemaHelper", "helper"));
        fragment.addSource(new OSourceSymbol(" = "));
        fragment.addSource(new OSourceStaticNewInstance("OSchemaHelper", "bind", singletonList("db")));
        fragment.addSource(new OSourceSymbol(";\n"));

        fragment.addSource(new OSourceCall("helper", "oClass", "\"TestClass\""));
        fragment.addSource(new OSourceSymbol("\n"));
        fragment.addSource(new OSourceSpace(4));
        fragment.addSource(new OSourceChainCall("oProperty", "\"test\"", "OType.STRING", "0"));
        fragment.addSource(new OSourceSymbol("\n"));
        fragment.addSource(new OSourceSpace(4));
        fragment.addSource(new OSourceChainCall("oProperty", "\"name\"", "OType.STRING", "10"));
        fragment.addSource(new OSourceSymbol(";\n"));

        String expectedSrc = "OSchemaHelper helper = OSchemaHelper.bind(db);\n" +
                "helper.oClass(\"TestClass\")\n" +
                "    .oProperty(\"test\", OType.STRING, 0)\n" +
                "    .oProperty(\"name\", OType.STRING, 10);\n";

        assertEquals(expectedSrc, fragment.toJavaSrc());

    }
}
