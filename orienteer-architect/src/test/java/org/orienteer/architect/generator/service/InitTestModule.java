package org.orienteer.architect.generator.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.OArchitectOProperty;

import java.util.Arrays;
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
     * employees - LinkList (inverse with Employee.workPlace)
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
        OArchitectOClass workPlace = new OArchitectOClass("WorkPlace");

        OArchitectOProperty workPlaceProp = new OArchitectOProperty("workPlace", OType.LINK);
        OArchitectOProperty employees = new OArchitectOProperty("employees", OType.LINKLIST);

        workPlaceProp.setLinkedClass("WorkPlace")
                .setInversePropertyEnable(true)
                .setInverseProperty(employees);

        employees.setLinkedClass("Employee")
                .setInversePropertyEnable(true)
                .setInverseProperty(workPlaceProp);

        employee.setProperties(asList(
                new OArchitectOProperty("name", OType.STRING).setOrder(0),
                new OArchitectOProperty("id", OType.INTEGER).setOrder(10),
                workPlaceProp.setOrder(20),
                new OArchitectOProperty("testLink", OType.LINK).setOrder(30).setLinkedClass("WorkPlace")
        ));

        workPlace.setProperties(asList(
                new OArchitectOProperty("name", OType.STRING).setOrder(0),
                new OArchitectOProperty("id", OType.INTEGER).setOrder(10),
                employees.setOrder(20)
        ));

        return Arrays.asList(employee, workPlace);
    }
}
