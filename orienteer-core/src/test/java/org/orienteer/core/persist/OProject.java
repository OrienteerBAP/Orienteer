package org.orienteer.core.persist;

import com.orientechnologies.orient.core.annotation.OVersion;
import lombok.Data;
import ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.notnull.ONotNull;

@Persistent
@Data
public class OProject {

    @ONotNull
    private String name;

    @ONotNull
    private String description;

    @OVersion
    private Long version;
}
