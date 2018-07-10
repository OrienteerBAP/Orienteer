package org.orienteer.users.service;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.record.impl.ODocument;

@ImplementedBy(OUserDBServiceImpl.class)
public interface IOUsersDBService {
    public ODocument getDefaultOrienteerUserPerspective();
    public ORole getRoleByName(String name);
}
