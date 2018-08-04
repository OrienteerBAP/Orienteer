package org.orienteer.core;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.model.OProject;
import org.orienteer.junit.OrienteerTestRunner;
import ru.vyarus.guice.persist.orient.db.PersistentContext;

import java.util.List;

@RunWith(OrienteerTestRunner.class)
@Slf4j
public class TestGuicePersistOrient {

    @Inject
    private PersistentContext<OObjectDatabaseTx> context;

    @Test
    public void testSaveModel() {
        OProject project = new OProject();
        project.setName("test name 2");
        project.setDescription("test description 2");
        saveProject(project);

        List<OProject> projects = getProjects();
        log.info("projects: {}", projects);
    }

    @Transactional
    void saveProject(OProject project) {
        context.getConnection().save(project);
    }

    @Transactional
    List<OProject> getProjects() {
        return context.getConnection().query(new OSQLSynchQuery<>("select from OProject"));
    }
}
