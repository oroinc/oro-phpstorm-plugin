package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.*;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Stream;

import static com.oroplatform.idea.oroplatform.Functions.toStream;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements.elementFilter;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getTextOfPhpString;

public class ObjectInitializationOptionsCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final InsertHandler<LookupElement> insertHandler;

    public ObjectInitializationOptionsCompletionProvider(InsertHandler<LookupElement> insertHandler) {
        this.insertHandler = insertHandler;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        toStream(YamlPsiElements.getOrderedAncestors(parameters.getPosition()))
            .flatMap(elementFilter(YAMLKeyValue.class))
            .flatMap(keyValue -> Stream.of(keyValue.getReferences()))
            .flatMap(reference -> toStream(reference.resolve()))
            .flatMap(elementFilter(PhpClass.class))
            .limit(1) // get only first class reference from the closest KeyValue
            .flatMap(this::optionsFromPhpClass)
            .map(option -> LookupElementBuilder.create(option).withInsertHandler(insertHandler))
            .forEach(result::addElement);
    }

    private Stream<String> optionsFromPhpClass(PhpClass phpClass) {
        return phpClass.getMethods().stream()
            .flatMap(this::optionsFromMethod);
    }

    private Stream<String> optionsFromMethod(Method method) {
        final Collection<String> results = new LinkedList<>();
        final boolean next = true;
        PsiTreeUtil.processElements(method, element -> {
            if(element instanceof MethodReference) {
                final MethodReference methodReference = (MethodReference) element;
                if("getOptions".equals(methodReference.getName())) return next;
                if(methodReference.getParameters().length < 2) return next;
                getTextOfPhpString(methodReference.getParameters()[1]).ifPresent(results::add);
            } else if(element instanceof ArrayAccessExpression) {
                final ArrayAccessExpression array = (ArrayAccessExpression) element;
                final PhpPsiElement value = array.getValue();
                final ArrayIndex index = array.getIndex();

                if(value == null || index == null || !"options".equals(value.getName())) return next;
                if(value instanceof Variable && !"initialize".equals(method.getName())) return next;
                getTextOfPhpString(index.getValue()).ifPresent(results::add);
            }
            return next;
        });

        return results.stream();
    }
}
