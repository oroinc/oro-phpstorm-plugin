package com.oroplatform.idea.oroplatform.processor;

import com.intellij.util.Processor;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.ArrayList;
import java.util.Collection;

public class SubclassCollector implements Processor<PhpClass> {
    private final Collection<PhpClass> phpClasses = new ArrayList<>();

    @Override
    public boolean process(PhpClass phpClass) {
        if (!phpClasses.contains(phpClass)) {
            phpClasses.add(phpClass);
        }
        return true;
    }

    public Collection<PhpClass> getPhpClasses() {
        return phpClasses;
    }
}
