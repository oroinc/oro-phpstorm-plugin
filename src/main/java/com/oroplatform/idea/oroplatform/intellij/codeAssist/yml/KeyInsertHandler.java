package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;

class KeyInsertHandler implements InsertHandler<LookupElement> {
    static final InsertHandler<LookupElement> INSTANCE = new KeyInsertHandler();

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        final Document document = context.getDocument();
        final Editor editor = context.getEditor();

        int lastCharPos = lastCharPositionIgnoringSpace(document, context.getTailOffset());
        if(document.getCharsSequence().charAt(lastCharPos) != ':') {
            document.insertString(context.getTailOffset(), ": ");
            editor.getCaretModel().moveToOffset(context.getTailOffset());
        } else {
            editor.getCaretModel().moveToOffset(lastCharPositionIgnoringSpace(document, lastCharPos + 1));
        }

//        TODO: turn off because more sophisticated logic here is needed. AutoPopup should be shown only for choices and references
//        AutoPopupController.getInstance(editor.getProject()).scheduleAutoPopup(editor);
    }

    private int lastCharPositionIgnoringSpace(Document document, int tailOffset) {
        CharSequence charsSequence = document.getCharsSequence();

        for(int i=tailOffset; i<charsSequence.length(); i++) {
            char charAt = charsSequence.charAt(i);
            if(charAt != ' ') {
                return i;
            }
        }

        return Math.min(tailOffset, charsSequence.length() - 1);
    }
}
