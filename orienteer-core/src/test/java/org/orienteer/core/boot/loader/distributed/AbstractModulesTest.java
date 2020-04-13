package org.orienteer.core.boot.loader.distributed;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.junit.After;
import org.orienteer.core.boot.loader.internal.OModulesMicroFrameworkConfig;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class AbstractModulesTest {
	
	@Inject
	private OModulesMicroFrameworkConfig config;

    @After
    public void removeModulesFolder() throws Exception {
        Files.walkFileTree(config.getPathToModulesFolder(), new SimpleFileVisitor<Path>() {
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
