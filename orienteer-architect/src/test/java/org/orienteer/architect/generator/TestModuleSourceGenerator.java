package org.orienteer.architect.generator;

import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.generator.GeneratorMode;
import org.orienteer.architect.model.generator.OModuleSource;
import org.orienteer.architect.model.generator.OSourceGeneratorConfig;
import org.orienteer.architect.service.ISourceGenerator;
import org.orienteer.junit.OrienteerTestRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.orienteer.architect.generator.util.GeneratorAssertUtil.assertNextLine;
import static org.orienteer.architect.generator.util.GeneratorAssertUtil.assertNextPrettyLine;

@RunWith(OrienteerTestRunner.class)
public class TestModuleSourceGenerator {

    @Inject
    private List<OArchitectOClass> classes;

    @Inject
    private ISourceGenerator generator;

    @Test
    public void testGenerateModuleSource() throws IOException {
        OSourceGeneratorConfig config = new OSourceGeneratorConfig();
        config.setMode(GeneratorMode.MODULE);
        config.setClasses(classes);

        Optional<OModuleSource> optSource = generator.generateSource(config);
        assertTrue("Generated source not present", optSource.isPresent());

        OModuleSource source = optSource.get();
        assertEquals(GeneratorMode.MODULE.getName(), source.getName());


        assertNotPrettyPrint(new BufferedReader(new StringReader(source.getSrc())));
        assertPrettyPrint(new BufferedReader(new StringReader(source.getSrc())));
    }

    private void assertNotPrettyPrint(BufferedReader reader) throws IOException {
        assertNextLine("", reader);
        assertNextLine("public static final String EMPLOYEE_CLASS_NAME = \"Employee\";", reader);
        assertNextLine("public static final String EMPLOYEE_PROP_NAME = \"name\";", reader);
        assertNextLine("public static final String EMPLOYEE_PROP_ID = \"id\";", reader);
        assertNextLine("public static final String EMPLOYEE_PROP_WORKPLACE = \"workPlace\";", reader);
        assertNextLine("", reader);
        assertNextLine("public static final String WORKPLACE_CLASS_NAME = \"WorkPlace\";", reader);
        assertNextLine("public static final String WORKPLACE_PROP_NAME = \"name\";", reader);
        assertNextLine("public static final String WORKPLACE_PROP_ID = \"id\";", reader);
        assertNextLine("public static final String WORKPLACE_PROP_EMPLOYEES = \"employees\";", reader);
        assertNextLine("", reader);
        assertNextLine("", reader);
        assertNextLine("", reader);
        assertNextLine("OSchemaHelper helper = OSchemaHelper.bind(db);", reader);
        assertNextLine("helper.oClass(EMPLOYEE_CLASS_NAME)", reader);
        assertNextLine(".oProperty(EMPLOYEE_PROP_NAME, OType.STRING, 0)", reader);
        assertNextLine(".oProperty(EMPLOYEE_PROP_ID, OType.INTEGER, 10)", reader);
        assertNextLine(".oProperty(EMPLOYEE_PROP_WORKPLACE, OType.LINK, 20);", reader);
        assertNextLine("", reader);
        assertNextLine("helper.oClass(WORKPLACE_CLASS_NAME)", reader);
        assertNextLine(".oProperty(WORKPLACE_PROP_NAME, OType.STRING, 0)", reader);
        assertNextLine(".oProperty(WORKPLACE_PROP_ID, OType.INTEGER, 10)", reader);
        assertNextLine(".oProperty(WORKPLACE_PROP_EMPLOYEES, OType.LINKLIST, 20);", reader);
        assertNextLine("", reader);
        assertNextLine("", reader);
        assertNextLine("", reader);
        assertNextLine("helper.setupRelationship(EMPLOYEE_CLASS_NAME, EMPLOYEE_PROP_WORKPLACE, WORKPLACE_CLASS_NAME, WORKPLACE_PROP_EMPLOYEES);", reader);
    }

    private void assertPrettyPrint(BufferedReader reader) throws IOException {
        assertNextPrettyLine("", reader);
        assertNextPrettyLine("public static final String EMPLOYEE_CLASS_NAME = \"Employee\";", reader);
        assertNextPrettyLine("public static final String EMPLOYEE_PROP_NAME = \"name\";", reader);
        assertNextPrettyLine("public static final String EMPLOYEE_PROP_ID = \"id\";", reader);
        assertNextPrettyLine("public static final String EMPLOYEE_PROP_WORKPLACE = \"workPlace\";", reader);
        assertNextPrettyLine("", reader);
        assertNextPrettyLine("public static final String WORKPLACE_CLASS_NAME = \"WorkPlace\";", reader);
        assertNextPrettyLine("public static final String WORKPLACE_PROP_NAME = \"name\";", reader);
        assertNextPrettyLine("public static final String WORKPLACE_PROP_ID = \"id\";", reader);
        assertNextPrettyLine("public static final String WORKPLACE_PROP_EMPLOYEES = \"employees\";", reader);
        assertNextPrettyLine("", reader);
        assertNextPrettyLine("", reader);
        assertNextPrettyLine("", reader);
        assertNextPrettyLine("OSchemaHelper helper = OSchemaHelper.bind(db);", reader);
        assertNextPrettyLine("helper.oClass(EMPLOYEE_CLASS_NAME)", reader);
        assertNextPrettyLine("    .oProperty(EMPLOYEE_PROP_NAME, OType.STRING, 0)", reader);
        assertNextPrettyLine("    .oProperty(EMPLOYEE_PROP_ID, OType.INTEGER, 10)", reader);
        assertNextPrettyLine("    .oProperty(EMPLOYEE_PROP_WORKPLACE, OType.LINK, 20);", reader);
        assertNextPrettyLine("", reader);
        assertNextPrettyLine("helper.oClass(WORKPLACE_CLASS_NAME)", reader);
        assertNextPrettyLine("    .oProperty(WORKPLACE_PROP_NAME, OType.STRING, 0)", reader);
        assertNextPrettyLine("    .oProperty(WORKPLACE_PROP_ID, OType.INTEGER, 10)", reader);
        assertNextPrettyLine("    .oProperty(WORKPLACE_PROP_EMPLOYEES, OType.LINKLIST, 20);", reader);
        assertNextPrettyLine("", reader);
        assertNextPrettyLine("", reader);
        assertNextPrettyLine("", reader);
        assertNextPrettyLine("helper.setupRelationship(EMPLOYEE_CLASS_NAME, EMPLOYEE_PROP_WORKPLACE, WORKPLACE_CLASS_NAME, WORKPLACE_PROP_EMPLOYEES);", reader);
    }
}
