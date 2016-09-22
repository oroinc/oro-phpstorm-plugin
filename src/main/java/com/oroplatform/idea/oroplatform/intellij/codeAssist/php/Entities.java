package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.oroplatform.idea.oroplatform.symfony.Entity;

import java.util.Collection;
import java.util.LinkedList;

class Entities {

    private final Project project;

    private Entities(Project project) {
        this.project = project;
    }

    static Entities instance(Project project) {
        return new Entities(project);
    }

    Collection<Entity> findEntities(String className) {
        final PhpIndex phpIndex = PhpIndex.getInstance(project);
        final Collection<Entity> entities = new LinkedList<Entity>();

        for (PhpClass phpClass : phpIndex.getClassesByFQN(className)) {
            final Entity entity = Entity.fromFqn(phpClass.getPresentableFQN());

            if(entity != null) {
                entities.add(entity);
            }
        }

        return entities;
    }
}
