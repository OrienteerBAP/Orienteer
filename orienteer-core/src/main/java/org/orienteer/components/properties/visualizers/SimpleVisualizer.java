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
package org.orienteer.components.properties.visualizers;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.orienteer.components.properties.DisplayMode;

import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class SimpleVisualizer extends AbstractSimpleVisualizer {

    private final Class<? extends Component> viewComponentClass;
    private final Class<? extends Component> editComponentClass;

    public SimpleVisualizer(String name, Class<? extends Component> viewComponentClass, Class<? extends Component> editComponentClass, OType... supportedTypes) {
        this(name, viewComponentClass, editComponentClass, Arrays.asList(supportedTypes));
    }

    public SimpleVisualizer(String name, Class<? extends Component> viewComponentClass, Class<? extends Component> editComponentClass, Collection<OType> supportedTypes) {
        this(name, false, viewComponentClass, editComponentClass, supportedTypes);
    }

    public SimpleVisualizer(String name, boolean extended, Class<? extends Component> viewComponentClass, Class<? extends Component> editComponentClass, OType... supportedTypes) {
        this(name, extended, viewComponentClass, editComponentClass, Arrays.asList(supportedTypes));
    }

    public SimpleVisualizer(String name, boolean extended, Class<? extends Component> viewComponentClass, Class<? extends Component> editComponentClass, Collection<OType> supportedTypes) {
        super(name, extended, supportedTypes);
        Args.notNull(name, "name");
        Args.notNull(viewComponentClass, "viewComponentClass");
        Args.notNull(editComponentClass, "editComponentClass");
        Args.notNull(supportedTypes, "supportedTypes");
        Args.notEmpty(supportedTypes, "supportedTypes");
        this.viewComponentClass = viewComponentClass;
        this.editComponentClass = editComponentClass;
    }

    @Override
    public <V> Component createComponent(String id, DisplayMode mode,
            IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
        Class<? extends Component> componentClass = DisplayMode.EDIT.equals(mode) ? editComponentClass : viewComponentClass;
        try {
            Constructor<? extends Component> constructor = componentClass.getConstructor(String.class, IModel.class, IModel.class);
            return constructor.newInstance(id, documentModel, propertyModel);
        } catch (NoSuchMethodException e) {
            return createComponent(id, mode, valueModel);
        } catch (Exception e) {
            throw new WicketRuntimeException("Can't create component", e);
        }
    }

    public <T> Component createComponent(String id, DisplayMode mode,
            IModel<T> model) {
        Class<? extends Component> componentClass = DisplayMode.EDIT.equals(mode) ? editComponentClass : viewComponentClass;
        try {
            return componentClass.getConstructor(String.class, IModel.class).newInstance(id, model);
        } catch (Exception e) {
            throw new WicketRuntimeException("Can't create component", e);
        }
    }

}
