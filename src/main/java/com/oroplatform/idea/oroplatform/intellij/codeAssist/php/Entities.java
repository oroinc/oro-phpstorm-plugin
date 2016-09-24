package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.project.Project;
import com.oroplatform.idea.oroplatform.symfony.Entity;

import java.util.Collection;
import java.util.LinkedList;

class Entities {

    private Entities() {
    }

    static Entities instance(Project project) {
        return new Entities();
    }

    Collection<Entity> findEntities(String className) {
        final Collection<Entity> entities = new LinkedList<Entity>();

        final Entity entity = Entity.fromFqn(className);

        if(entity != null) {
            entities.add(entity);
        }

        return entities;
    }
}
