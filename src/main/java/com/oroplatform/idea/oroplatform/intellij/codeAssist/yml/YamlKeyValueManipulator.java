package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;

class YamlKeyValueManipulator extends AbstractElementManipulator<YAMLKeyValue> {
    @Override
    public YAMLKeyValue handleContentChange(@NotNull YAMLKeyValue element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return element;
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull YAMLKeyValue element) {
        final boolean containsQuote = element.getValueText().contains("\'") || element.getValueText().contains("\"");
        final int length = element.getTextLength() - element.getValueText().length() - (containsQuote ? 2 : 0);
        return new TextRange(length, element.getTextLength());
    }
}
