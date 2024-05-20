package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.oroplatform.idea.oroplatform.symfony.Entity;

import java.util.Collection;

public interface EntityExtensions {
    public Collection<ExFileBasedEntityExtensions.ExtensionMethod> getMethods(Entity entity);
}
