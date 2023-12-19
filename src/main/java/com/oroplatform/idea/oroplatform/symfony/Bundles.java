package com.oroplatform.idea.oroplatform.symfony;

import com.intellij.util.Processor;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.ArrayList;
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
        SubclassCollector collector = new SubclassCollector();
        phpIndex.processAllSubclasses("\\Symfony\\Component\\HttpKernel\\Bundle\\Bundle", collector);
        return collector.getPhpClasses();
    }

    private static class SubclassCollector implements Processor<PhpClass> {
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
}
