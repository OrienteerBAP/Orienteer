package org.orienteer.core.service.impl;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.metadata.security.OSecurityRole;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializablePredicate;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.service.IFilterPredicateFactory;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation for {@link IFilterPredicateFactory}
 */
public class DefaultFilterPredicateFactory implements IFilterPredicateFactory {

    @Override
    public SerializablePredicate<OClass> getPredicateByTarget(IModel<Boolean> showAllModel) {
        return (input) -> {
            Boolean showAll = showAllModel.getObject();
            return showAll == null || showAll || OClassDomain.BUSINESS.equals(CustomAttribute.DOMAIN.getValue(input));
        };
    }

    @Override
    public SerializablePredicate<OClass> getPredicateByOperation(int operation) {
        OSecurityUser user = OrienteerWebSession.get().getUser();
        return user != null ? (input) -> user.checkIfAllowed(ORule.ResourceGeneric.CLASS, input.getName(), operation) != null 
        		: (i) -> false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializablePredicate<OClass> getPredicateForClassesView(IModel<Boolean> showAll) {
        return compose(getPredicateByOperation(2), getPredicateByTarget(showAll));
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializablePredicate<OClass> getPredicateForClassesSearch() {
        return compose(getPredicateByOperation(2), getPredicateByTarget(Model.of(false)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public SerializablePredicate<OProperty> getPredicateForListProperties() {
        OSecurityUser user = OrienteerWebSession.get().getEffectiveUser();
        Set<String> userRoles = user.getRoles().stream().map(OSecurityRole::getName).collect(Collectors.toSet());

        return compose(
                prop -> !(Boolean)CustomAttribute.HIDDEN.getValue(prop),
                prop -> user.checkIfAllowed(ORule.ResourceGeneric.CLASS, prop.getOwnerClass().getName(), OrientPermission.READ.getPermissionFlag()) != null,
                prop -> {
                    String hiddenRoles = CustomAttribute.PROP_HIDDEN_FROM.getValue(prop);
                    return hiddenRoles == null || Arrays.stream(hiddenRoles.split("\\,")).noneMatch(userRoles::contains);
                }
        );
    }

    @Override
    public SerializablePredicate<OProperty> getPredicateForTableProperties() {
        return CustomAttribute.DISPLAYABLE::getValue;
    }
}
