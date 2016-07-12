package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpClassReference;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import com.oroplatform.idea.oroplatform.schema.Scalar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PhpClassReferenceProvider extends PsiReferenceProvider {

    private final Scalar.PhpClass phpClass;
    private final InsertHandler<LookupElement> insertHandler;

    public PhpClassReferenceProvider(Scalar.PhpClass phpClass, InsertHandler<LookupElement> insertHandler) {
        this.phpClass = phpClass;
        this.insertHandler = insertHandler;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLKeyValue) {
            final HashSet<String> skippedClassNames = getClassNamesFromSiblings((YAMLKeyValue) element);

            return new PsiReference[] {
                new PhpClassReference(((YAMLKeyValue) element).getKey(), phpClass, ((YAMLKeyValue) element).getKeyText(), insertHandler, skippedClassNames)
            };

        // skip scalar element in key context
        } else if(element instanceof YAMLScalar && context.get("key") == null) {
            YAMLMapping mapping = YamlPsiElements.getFirstMapping(element);
            final Set<String> skippedClassNames = mapping == null ? Collections.<String>emptySet() : getClassNames(mapping);

            return new PsiReference[]{new PhpClassReference(element, phpClass, ((YAMLScalar) element).getTextValue(), insertHandler, skippedClassNames)};
        }

        return new PsiReference[0];
    }

    @NotNull
    private HashSet<String> getClassNamesFromSiblings(@NotNull YAMLKeyValue element) {
        final YAMLMapping mapping = element.getParentMapping();

        return getClassNames(mapping);
    }

    @NotNull
    private HashSet<String> getClassNames(YAMLMapping mapping) {
        final HashSet<String> skippedClassNames = new HashSet<String>();
        for (YAMLKeyValue keyValue : mapping.getKeyValues()) {
            skippedClassNames.add("\\"+keyValue.getKeyText());
        }
        return skippedClassNames;
    }
}
