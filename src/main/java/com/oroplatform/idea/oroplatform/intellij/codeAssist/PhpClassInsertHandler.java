package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;

public class PhpClassInsertHandler implements InsertHandler<LookupElement> {

    public static final InsertHandler<LookupElement> INSTANCE = new PhpClassInsertHandler();

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        if(item.getObject() instanceof PhpClass) {
            PhpClass phpClass = (PhpClass) item.getObject();
            final char lastChar = context.getDocument().getCharsSequence().charAt(context.getTailOffset());
            final boolean isAroundQuotes = lastChar == '\"' || lastChar == '\'';
            if(!isAroundQuotes) {
                context.getDocument().insertString(context.getTailOffset(), "\"");
            }
            context.getDocument().insertString(context.getStartOffset(), (isAroundQuotes ? "" : "\"")+getFixedFQNamespace(phpClass));
        }
    }

    private static String getFixedFQNamespace(PhpClass phpClass) {
        return escapeSlashes(StringUtil.trimStart(phpClass.getNamespaceName(), "\\"));
    }

    private static String escapeSlashes(String s) {
        return s.replace("\\", "\\\\");
    }
}
