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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicLookupElement that = (BasicLookupElement) o;

        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
