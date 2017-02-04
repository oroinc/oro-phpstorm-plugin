package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;

public class FixMissingQuoteInsertHandler implements InsertHandler<LookupElement> {
    public static final InsertHandler<LookupElement> INSTANCE = new FixMissingQuoteInsertHandler();

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        final DocumentOps document = new DocumentOps(context.getDocument());

        final int firstCharPos = document.firstCharPositionBefore(context.getTailOffset());
        final int lastCharPos = document.lastCharPositionIgnoringSpaceAfter(context.getTailOffset());
        final char firstChar = document.charAt(firstCharPos);
        final char lastChar = document.charAt(lastCharPos);

        if(isQuoteChar(firstChar) && !isQuoteChar(lastChar)) {
            document.insertString(context.getTailOffset(), firstChar+"");
        }
    }

    private boolean isQuoteChar(final char c) {
        return c == '\'' || c == '"';
    }
}
