package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.lookup.LookupElement;
import org.jetbrains.annotations.NotNull;

class BasicLookupElement extends LookupElement {

    private final String value;

    BasicLookupElement(String value) {
        this.value = value;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return value;
    }
}
