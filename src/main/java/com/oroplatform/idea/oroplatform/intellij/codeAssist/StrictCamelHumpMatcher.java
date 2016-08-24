package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import org.jetbrains.annotations.NotNull;

class StrictCamelHumpMatcher extends CamelHumpMatcher {
    private final String prefixUpperLetters;

    StrictCamelHumpMatcher(@NotNull String prefix) {
        super(prefix);
        this.prefixUpperLetters = getUpperLetters(prefix);
    }

    @NotNull
    private String getUpperLetters(@NotNull String prefix) {
        final StringBuilder prefixUpperLetters = new StringBuilder();

        for (int i=0; i<prefix.length(); i++) {
            char c = prefix.charAt(i);
            if(Character.isUpperCase(c)) {
                prefixUpperLetters.append(c);
            }
        }
        return prefixUpperLetters.toString();
    }

    @Override
    public boolean isStartMatch(String name) {
        return super.isStartMatch(name) && getUpperLetters(name).equals(prefixUpperLetters);
    }
}
