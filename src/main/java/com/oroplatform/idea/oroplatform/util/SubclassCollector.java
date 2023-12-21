package com.oroplatform.idea.oroplatform.util;

import com.intellij.util.Processor;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class SubclassCollector {

    private final PhpIndex phpIndex;

    public SubclassCollector(@NotNull PhpIndex phpIndex) {
        this.phpIndex = phpIndex;
    }

    public Collection<PhpClass> getAllSubclasses(String phpClassPath) {
        SubclassCollectionProcessor processor = new SubclassCollectionProcessor();
        phpIndex.processAllSubclasses(phpClassPath, processor);
        return processor.getPhpClasses();
    }

    private static class SubclassCollectionProcessor implements Processor<PhpClass> {
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
