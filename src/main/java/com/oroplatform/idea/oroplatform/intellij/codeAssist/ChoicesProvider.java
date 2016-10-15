package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.psi.PsiElement;

import javax.swing.*;
import java.util.Collection;

public interface ChoicesProvider {
    Collection<Choice> getChoices(PsiElement element);

    class Choice {
        private final String name;
        private final String description;
        private final Icon icon;

        public Choice(String name, String description) {
            this(name, description, null);
        }

        public Choice(String name, String description, Icon icon) {
            this.name = name;
            this.description = description;
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Icon getIcon() {
            return icon;
        }
    }
}
