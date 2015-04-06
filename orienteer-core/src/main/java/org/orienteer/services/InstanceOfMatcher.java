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
package org.orienteer.services;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;

public class InstanceOfMatcher<T> extends AbstractMatcher<TypeLiteral<?>> {

    private final Class<T> clazz;

    protected InstanceOfMatcher(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean matches(TypeLiteral<?> t) {
        return clazz.isAssignableFrom(t.getRawType());
    }

    public static <T> InstanceOfMatcher<T> createFor(Class<T> clazz) {
        return new InstanceOfMatcher<T>(clazz);
    }

}
