package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.oroplatform.idea.oroplatform.symfony.Entity;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EntityExtensions {

    private final VirtualFileSystem fileSystem;
    private final String extensionsPath;
    private static final Pattern METHOD_PATTERN = Pattern.compile("public function ([a-z-A-Z0-9_]+)");

    private EntityExtensions(VirtualFileSystem fileSystem, String extensionsPath) {
        this.fileSystem = fileSystem;
        this.extensionsPath = extensionsPath;
    }

    public static EntityExtensions instance(VirtualFileSystem fileSystem, String extensionsPath) {
        return new EntityExtensions(fileSystem, extensionsPath);
    }

    Collection<ExtensionMethod> getMethods(Entity entity) {
        final String filename = "EX_" + entity.getBundle().getName() + "_" + entity.getSimpleName()+".php";
        final VirtualFile extensionsDir = fileSystem.findFileByPath(extensionsPath);

        if(extensionsDir == null) return Collections.emptyList();

        final VirtualFile classFile = extensionsDir.findChild(filename);

        if(classFile == null) return Collections.emptyList();

        //TODO: cache
        try {
            final String contents = new String(classFile.contentsToByteArray(), classFile.getCharset());
            final Collection<ExtensionMethod> methods = new LinkedList<ExtensionMethod>();
            final Matcher matcher = METHOD_PATTERN.matcher(contents);
            while(matcher.find()) {
                methods.add(new ExtensionMethod(matcher.group(1)));
            }
            return methods;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    static class ExtensionMethod {
        final String name;

        ExtensionMethod(String name) {
            this.name = name;
        }
    }
}
