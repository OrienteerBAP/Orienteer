package org.orienteer.architect.generator.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.OArchitectOProperty;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class InitTestModule extends AbstractModule {
    /**
     * Employee
     * name - String
     * id - Integer
     * roles - LinkedList
     * workPlace - Link (inverse with WorkPlace.employees)
     *
     * WorkPlace
     * name - String
     * id - Integer
     * employees - LinkedList (inverse with Employee.workPlace)
     *
     * EmployeeRole
     * name - String
     * id - Integer
     * privilege - EmbeddedMap
     */
    @Provides
    @Singleton
    public List<OArchitectOClass> provideTestClasses() {
        OArchitectOClass employee = new OArchitectOClass("Employee");

        employee.setProperties(asList(
                new OArchitectOProperty("name", OType.STRING).setOrder(0),
                new OArchitectOProperty("id", OType.INTEGER).setOrder(10)
        ));

        return Collections.singletonList(employee);
    }
}
