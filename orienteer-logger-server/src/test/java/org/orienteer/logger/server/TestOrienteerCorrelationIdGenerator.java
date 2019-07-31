package org.orienteer.logger.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.logger.IOCorrelationIdGenerator;
import org.orienteer.logger.server.service.correlation.OLogObj;
import org.orienteer.logger.server.service.correlation.OrienteerCorrelationIdGenerator;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(OrienteerTestRunner.class)
public class TestOrienteerCorrelationIdGenerator {

    private IOCorrelationIdGenerator generator;

    @Before
    public void init() {
        generator = new OrienteerCorrelationIdGenerator();
    }

    @Test
    public void testCorrelationIdForStrings() {
        String str = "Test";
        String str2 = "Test 2";

        assertEquals(generator.generate(str), generator.generate(str));
        assertEquals(generator.generate(str), generator.generate(str2));
    }

    @Test
    public void testLogObjects() {
        String str = "Test";
        Integer number = 123;

        OLogObj test1Str = OLogObj.of("test1", str);
        OLogObj test1Number = OLogObj.of("test1", number);

        OLogObj test2Str = OLogObj.of("test2", str);
        OLogObj test2Number = OLogObj.of("test2", number);

        assertEquals(generator.generate(test1Str), generator.generate(test1Number));
        assertEquals(generator.generate(test2Str), generator.generate(test2Number));

        assertNotEquals(generator.generate(test1Str), generator.generate(test2Str));
        assertNotEquals(generator.generate(test1Number), generator.generate(test2Number));
    }
}
