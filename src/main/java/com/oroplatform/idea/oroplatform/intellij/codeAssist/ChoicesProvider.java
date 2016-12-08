package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.schema.DefaultValueDescriptor;

import javax.swing.*;
import java.util.Collection;

public interface ChoicesProvider {
    Collection<Choice> getChoices(PsiElement element);

    class Choice {
        private final String name;
        private final DefaultValueDescriptor defaultValueDescriptor;
        private final String description;
        private final Icon icon;
        private final boolean deprecated;

        Choice(String name, String description) {
            this(name, description, null, null, false);
        }

        public Choice(String name, String description, DefaultValueDescriptor defaultValueDescriptor, boolean deprecated) {
            this(name, description, null, defaultValueDescriptor, deprecated);
        }

        Choice(String name, String description, Icon icon) {
            this(name, description, icon, null, false);
        }

        private Choice(String name, String description, Icon icon, DefaultValueDescriptor defaultValueDescriptor, boolean deprecated) {
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.defaultValueDescriptor = defaultValueDescriptor;
            this.deprecated = deprecated;
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

        public boolean isDeprecated() {
            return deprecated;
        }

        public DefaultValueDescriptor getDefaultValueDescriptor() {
            return defaultValueDescriptor;
        }
    }
}
