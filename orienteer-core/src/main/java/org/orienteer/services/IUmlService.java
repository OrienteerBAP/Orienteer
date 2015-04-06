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

import org.orienteer.services.impl.PlantUmlService;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

@ImplementedBy(PlantUmlService.class)
public interface IUmlService {

    public String describe(OSchema schema);

    public String describe(OClass oClass);

    public String describe(boolean goUp, boolean goDown, OClass... oClass);

    public String describe(OProperty oProperty);

    public String describeAsImage(OSchema schema);

    public String describeAsImage(OClass oClass);

    public String describeAsImage(boolean goUp, boolean goDown, OClass... oClass);

    public String describeAsImage(OProperty oProperty);

    public String asImage(String content);

    public boolean isUmlDebugEnabled();
}
