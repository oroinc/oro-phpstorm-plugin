package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;

class AutoPopupInsertHandler implements InsertHandler<LookupElement> {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        if(context.getEditor().getProject() != null) {
            AutoPopupController.getInstance(context.getEditor().getProject()).scheduleAutoPopup(context.getEditor());
        }
    }
}
