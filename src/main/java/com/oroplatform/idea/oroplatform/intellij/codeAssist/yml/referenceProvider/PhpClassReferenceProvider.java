package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpClassReference;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import com.oroplatform.idea.oroplatform.schema.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.util.*;
import java.util.stream.Collectors;

public class PhpClassReferenceProvider extends PsiReferenceProvider {

    private final PhpClass phpClass;
    private final InsertHandler<LookupElement> insertHandler;

    public PhpClassReferenceProvider(PhpClass phpClass, InsertHandler<LookupElement> insertHandler) {
        this.phpClass = phpClass;
        this.insertHandler = insertHandler;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLKeyValue) {
            final YAMLKeyValue keyValue = (YAMLKeyValue) element;
            final Set<String> skippedClassNames = concat(getClassNamesFromSiblings(keyValue), getExcludedClassNames(element));

            return new PsiReference[] {
                new PhpClassReference(keyValue.getKey(), phpClass, keyValue.getKeyText(), insertHandler, skippedClassNames)
            };

        // skip scalar element in key context
        } else if(element instanceof YAMLScalar && context.get("key") == null) {
            final Set<String> classNamesFromParentSiblings = YamlPsiElements.getFirstMapping(element)
                .map(this::getClassNames)
                .orElseGet(Collections::emptySet);

            final Set<String> skippedClassNames = concat(classNamesFromParentSiblings, getExcludedClassNames(element));

            return new PsiReference[]{new PhpClassReference(element, phpClass, StringUtil.stripQuotesAroundValue(element.getText()), insertHandler, skippedClassNames)};
        }

        return new PsiReference[0];
    }

    private Collection<String> getExcludedClassNames(@NotNull PsiElement element) {
        return phpClass.getExcludedClassesPath()
                    .map(excludedClassesPath -> YamlPsiElements.getPropertyFrom(excludedClassesPath, element))
                    .orElse(Collections.emptyList());
    }

    @SafeVarargs
    private static Set<String> concat(Collection<String>... collections) {
        final Set<String> result = new HashSet<>();
        Arrays.stream(collections).forEach(result::addAll);

        return result;
    }

    @NotNull
    private Set<String> getClassNamesFromSiblings(@NotNull YAMLKeyValue element) {
        final YAMLMapping mapping = element.getParentMapping();

        return getClassNames(mapping);
    }

    @NotNull
    private Set<String> getClassNames(YAMLMapping mapping) {
        return mapping.getKeyValues().stream()
            .map(YAMLKeyValue::getKeyText)
            .collect(Collectors.toSet());
    }
}
