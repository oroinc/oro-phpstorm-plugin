package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

class PhpClassLookupElement extends com.jetbrains.php.completion.PhpClassLookupElement {
    private final Set<String> lookupStrings = new HashSet<>();

    PhpClassLookupElement(@NotNull PhpClass phpClass, boolean showNamespace, @Nullable InsertHandler<LookupElement> insertHandler) {
        super(phpClass, showNamespace, insertHandler);
        lookupStrings.add(phpClass.getName());
        final String fqn = StringUtil.trimLeading(phpClass.getFQN(), '\\');
        lookupStrings.add(fqn);
        lookupStrings.add(fqn.replace("\\", "\\\\"));
    }

    @NotNull
    @Override
    public Set<String> getAllLookupStrings() {
        return lookupStrings;
    }
}
