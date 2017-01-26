package com.oroplatform.idea.oroplatform.symfony;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.Collection;
import java.util.stream.Collectors;

public class Bundles {
    private final PhpIndex phpIndex;

    public Bundles(PhpIndex phpIndex) {
        this.phpIndex = phpIndex;
    }

    public Collection<Bundle> findAll() {
        return findBundleClasses().stream()
            .map(phpClass -> new Bundle(phpClass.getNamespaceName()))
            .collect(Collectors.toList());
    }

    public Collection<PhpClass> findBundleClasses() {
        return phpIndex.getAllSubclasses("\\Symfony\\Component\\HttpKernel\\Bundle\\Bundle");
    }
}
