package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.psi.PsiElement;

import java.util.Collection;
import java.util.LinkedList;

public class StaticChoicesProvider implements ChoicesProvider {
    private final Collection<Choice> choices = new LinkedList<>();

    public StaticChoicesProvider(String... choices) {
        for (String choice : choices) {
            this.choices.add(new Choice(choice, null));
        }
    }

    public StaticChoicesProvider(Collection<Choice> choices) {
        this.choices.addAll(choices);
    }

    @Override
    public Collection<Choice> getChoices(PsiElement element) {
        return choices;
    }
}
