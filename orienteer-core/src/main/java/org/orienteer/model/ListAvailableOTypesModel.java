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
package org.orienteer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class ListAvailableOTypesModel extends LoadableDetachableModel<List<OType>> {

    private final IModel<OProperty> propertyModel;
    private final static List<OType> WHOLE_LIST = orderTypes(Arrays.asList(OType.values()));
    private final static Map<OType, List<OType>> CACHE_ORDERED = new HashMap<OType, List<OType>>();

    public ListAvailableOTypesModel(IModel<OProperty> propertyModel) {
        this.propertyModel = propertyModel;
    }

    @Override
    protected List<OType> load() {
        OProperty property = propertyModel.getObject();

        return property == null || property instanceof IPrototype ? findAvailableOTypes(null) : findAvailableOTypes(property.getType());
    }

    protected List<OType> findAvailableOTypes(OType type) {
        if (type == null) {
            return WHOLE_LIST;
        } else {
            List<OType> ret = CACHE_ORDERED.get(type);
            if (ret == null) {
                ret = orderTypes(type.getCastable());
                CACHE_ORDERED.put(type, ret);
            }
            return ret;
        }
    }

    public static List<OType> orderTypes(Collection<OType> types) {
        List<OType> list = types instanceof List ? (List<OType>) types : new ArrayList<OType>(types);

        Collections.sort(list, new Comparator<OType>() {

            @Override
            public int compare(OType o1, OType o2) {
                return o1.name().compareTo(o2.name());
            }
        });
        return list;
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        propertyModel.detach();
    }

}
