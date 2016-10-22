package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.project.Project;
import com.oroplatform.idea.oroplatform.symfony.Entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

class Entities {

    private Entities() {
    }

    static Entities instance(Project project) {
        return new Entities();
    }

    Collection<Entity> findEntities(String className) {
        return Entity.fromFqn(className)
            .map(Arrays::asList)
            .orElseGet(Collections::emptyList);
    }
}
