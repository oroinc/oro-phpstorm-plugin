package com.oroplatform.idea.oroplatform.symfony;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.Collection;
import java.util.LinkedList;

public class Bundles {
    private final PhpIndex phpIndex;

    public Bundles(PhpIndex phpIndex) {
        this.phpIndex = phpIndex;
    }

    public Collection<Bundle> findAll() {
        final Collection<PhpClass> classes = phpIndex.getAllSubclasses("\\Symfony\\Component\\HttpKernel\\Bundle\\Bundle");
        final Collection<Bundle> bundles = new LinkedList<Bundle>();

        for (PhpClass phpClass : classes) {
            bundles.add(new Bundle(phpClass.getNamespaceName()));
        }

        return bundles;
    }
}
