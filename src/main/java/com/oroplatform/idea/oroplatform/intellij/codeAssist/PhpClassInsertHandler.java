package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;

class PhpClassInsertHandler implements InsertHandler<LookupElement> {

    static final InsertHandler<LookupElement> INSTANCE = new PhpClassInsertHandler();

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        if(item.getObject() instanceof PhpClass) {
            final PhpClass phpClass = (PhpClass) item.getObject();
            final boolean isAroundQuotes = isAroundQuotes(context);
            context.getDocument().insertString(context.getStartOffset(), getFixedFQNamespace(phpClass, isAroundQuotes));
        }
    }

    private boolean isAroundQuotes(InsertionContext context) {
        final CharSequence charsSequence = context.getDocument().getCharsSequence();

        if(context.getTailOffset() >= charsSequence.length()) {
            return false;
        }

        final char lastChar = charsSequence.charAt(context.getTailOffset());
        return lastChar == '\"' || lastChar == '\'';
    }

    private static String getFixedFQNamespace(PhpClass phpClass, boolean escapeSlashes) {
        final String namespace = StringUtil.trimStart(phpClass.getNamespaceName(), "\\");
        return escapeSlashes ? escapeSlashes(namespace) : namespace;
    }

    private static String escapeSlashes(String s) {
        return s.replace("\\", "\\\\");
    }
}
