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
package org.orienteer.junit.tests;

import static org.junit.Assert.assertTrue;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.orienteer.OrienteerWebApplication;
import org.orienteer.junit.OrienteerTester;

import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTester;

import com.google.inject.Inject;

public abstract class AbstractTestInjection {

    @Inject
    private WicketTester tester1;

    @Inject
    private WicketOrientDbTester tester2;

    @Inject
    private OrienteerTester tester3;

    @Inject
    private WebApplication app1;

    @Inject
    private OrientDbWebApplication app2;

    @Inject
    private OrienteerWebApplication app3;

    @Test
    public void testTesterInjection() {
        assertTrue(tester1 instanceof OrienteerTester);
        assertTrue(tester2 instanceof OrienteerTester);
        assertTrue(tester3 instanceof OrienteerTester);
        assertTrue(tester1 == tester2);
        assertTrue(tester2 == tester3);
        assertTrue(tester3 == tester1);
    }

    @Test
    public void testApplicationInjection() {
        assertTrue(app1 instanceof OrienteerWebApplication);
        assertTrue(app2 instanceof OrienteerWebApplication);
        assertTrue(app3 instanceof OrienteerWebApplication);
        assertTrue(app1 == app2);
        assertTrue(app2 == app3);
        assertTrue(app3 == app1);
    }
}
