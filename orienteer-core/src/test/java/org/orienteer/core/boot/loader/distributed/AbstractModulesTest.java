package org.orienteer.core.boot.loader.distributed;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.junit.After;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class AbstractModulesTest {
    @Inject
    @Named("orienteer.loader.libs.folder")
    private String libFolder;

    @After
    public void removeModulesFolder() throws Exception {
        Files.walkFileTree(Paths.get(libFolder), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                Files.delete(path);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
                Files.delete(path);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
