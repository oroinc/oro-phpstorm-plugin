package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLElementGenerator;
import org.jetbrains.yaml.psi.YAMLKeyValue;

//TODO: is necessary if custom InsertHandler is used?
public class YamlKeyValueManipulator extends AbstractElementManipulator<YAMLKeyValue> {
    @Override
    public YAMLKeyValue handleContentChange(@NotNull YAMLKeyValue element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        YAMLElementGenerator generator = YAMLElementGenerator.getInstance(element.getProject());
        YAMLKeyValue newElement = generator.createYamlKeyValue(element.getName());
        newElement.setValueText(newContent);
        return newElement;
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull YAMLKeyValue element) {
        final boolean containsQuote = element.getText().contains("\'") || element.getText().contains("\"");
        final int length = element.getTextLength() - element.getValueText().length() - (containsQuote ? 2 : 0);
        return new TextRange(length, element.getTextLength());
    }
}
