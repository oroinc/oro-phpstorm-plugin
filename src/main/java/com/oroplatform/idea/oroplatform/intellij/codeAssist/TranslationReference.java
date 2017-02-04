package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.*;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlScalarVisitor;
import com.oroplatform.idea.oroplatform.intellij.indexes.TranslationIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Collection;
import java.util.LinkedList;

import static com.oroplatform.idea.oroplatform.Functions.toStream;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements.elementFilter;

public class TranslationReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String text;
    private final InsertHandler<LookupElement> insertHandler;
    private TranslationIndex translationIndex;

    public TranslationReference(PsiElement element, String text, InsertHandler<LookupElement> insertHandler) {
        super(element);
        this.text = text;
        this.insertHandler = insertHandler;
        this.translationIndex = TranslationIndex.instance(myElement.getProject());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return translationIndex.findFilesContaining(text).stream()
            .flatMap(file -> toStream(PsiManager.getInstance(myElement.getProject()).findFile(file)))
            .flatMap(elementFilter(YAMLFile.class))
            .flatMap(file -> {
                final Collection<PsiElement> elements = new LinkedList<>();
                file.accept(new YamlScalarVisitor((trans, element) -> {
                    if (trans.equals(text)) {
                        elements.add(element);
                    }

                    return null;
                }));

                return elements.stream();
            })
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return translationIndex.findTranslations().stream()
            .map(element -> LookupElementBuilder.create(element).withInsertHandler(insertHandler))
            .toArray();
    }
}
