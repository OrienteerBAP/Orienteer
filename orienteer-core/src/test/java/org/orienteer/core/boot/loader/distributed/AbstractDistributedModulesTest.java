package org.orienteer.core.boot.loader.distributed;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.junit.After;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.OModulesMicroFrameworkConfig;
import org.orienteer.core.boot.loader.service.IModuleManager;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class AbstractDistributedModulesTest {

    @Inject
    @Named("config.1")
    protected OModulesMicroFrameworkConfig config1;

    @Inject
    @Named("config.2")
    protected OModulesMicroFrameworkConfig config2;


    @Inject
    @Named("internal.module.manager.1")
    protected InternalOModuleManager internalManager1;

    @Inject
    @Named("internal.module.manager.2")
    protected InternalOModuleManager internalManager2;

    @Inject
    @Named("module.manager.1")
    protected IModuleManager manager1;

    @Inject
    @Named("module.manager.2")
    protected IModuleManager manager2;

    @After
    public void removeModulesFolder() throws IOException {
        removeFolder(config1.getPathToModulesFolder());
        removeFolder(config2.getPathToModulesFolder());
        internalManager1.reindex(config1);
        internalManager2.reindex(config2);
    }

    private void removeFolder(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
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
