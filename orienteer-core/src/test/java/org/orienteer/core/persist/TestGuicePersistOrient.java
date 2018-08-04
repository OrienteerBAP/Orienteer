package org.orienteer.core.persist;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import ru.vyarus.guice.persist.orient.db.PersistentContext;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

@RunWith(OrienteerTestRunner.class)
@Slf4j
public class TestGuicePersistOrient {

    @Inject
    private PersistentContext<OObjectDatabaseTx> context;

    @Inject
    private IOProjectRepository repository;

    @Test
    public void testSaveModel() {
        OProject project = new OProject();
        project.setName("test name");
        project.setDescription("test description");

        saveProject(project);

        List<OProject> projects = repository.getProjects();
        assertTrue(!projects.isEmpty());

        deleteProjects(projects);

        assertTrue(repository.getProjects().isEmpty());
    }

    @Transactional
    void saveProject(OProject project) {
        context.getConnection().save(project);
    }

    @Transactional
    void deleteProjects(List<OProject> projects) {
        OObjectDatabaseTx connection = context.getConnection();
        projects.forEach(connection::delete);
    }
}
