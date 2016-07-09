package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;

class ColonAppenderInsertHandler implements InsertHandler<LookupElement> {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        context.getDocument().insertString(context.getTailOffset(), "::");
        context.getEditor().getCaretModel().moveToOffset(context.getTailOffset());
    }
}
