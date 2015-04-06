/**
 * Copyright (C) 2015 Ilia Naryzhny (phantom@ydn.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orienteer;

import org.junit.Test;
import org.orienteer.CustomAttributes;

import static org.junit.Assert.*;

public class TestOrienteerUtils {

    @Test
    public void testCustomEncode() throws Exception {
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
    public void testCustomDecode() throws Exception {
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
