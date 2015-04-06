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

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTester;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import static org.junit.Assert.*;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestOrineteerTestRunner extends AbstractTestInjection {

}
