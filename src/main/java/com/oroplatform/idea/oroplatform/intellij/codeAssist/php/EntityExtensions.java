package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.oroplatform.idea.oroplatform.symfony.Entity;

import java.util.Collection;

public interface EntityExtensions {
    public static final String EXTENSIONS_DIR_RELATIVE_PATH = "cache/dev/oro_entities/Extend/Entity";
    public Collection<PhpBasedEntityExtensions.ExtensionMethod> getMethods(Entity entity);
}
