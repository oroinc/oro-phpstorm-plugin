package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;

import java.util.LinkedList;
import java.util.List;

class ComposedInsertHandler implements InsertHandler<LookupElement> {
    private final List<InsertHandler<LookupElement>> handlers = new LinkedList<>();

    ComposedInsertHandler(List<InsertHandler<LookupElement>> handlers) {
        this.handlers.addAll(handlers);
    }

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        for (InsertHandler<LookupElement> handler : handlers) {
            handler.handleInsert(context, item);
        }
    }
}
