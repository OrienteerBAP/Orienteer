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
package org.orienteer.junit;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.runners.model.InitializationError;
import org.orienteer.services.OrienteerModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

public class OrienteerTestRunner extends GuiceTestRunner {

    public OrienteerTestRunner(Class<?> classToRun) throws InitializationError {
        super(classToRun, StaticInjectorProvider.INSTANCE);
    }

    @Override
    public Object createTest() {
        //Ensure that wicket tester and corresponding application started
        getInjector().getInstance(WicketTester.class);
        return super.createTest();
    }

}
