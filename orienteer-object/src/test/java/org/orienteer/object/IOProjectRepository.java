package org.orienteer.object;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.google.inject.persist.Transactional;
import org.orienteer.object.model.OProject;
import ru.vyarus.guice.persist.orient.repository.command.query.Query;

import java.util.List;

@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface IOProjectRepository {

    @Query("select from OProject")
    List<OProject> getProjects();
}
