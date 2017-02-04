package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Editor;
import com.oroplatform.idea.oroplatform.schema.DefaultValueDescriptor;

import java.util.Collection;

class KeyInsertHandler implements InsertHandler<LookupElement> {
    static final InsertHandler<LookupElement> INSTANCE = new KeyInsertHandler();
    private static final String KEY_COLON = ": ";

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        final DocumentOps document = new DocumentOps(context.getDocument());
        final Editor editor = context.getEditor();

        final int firstCharPos = document.firstCharPositionBefore(context.getTailOffset());
        final int lastCharPos = document.lastCharPositionIgnoringSpaceAfter(context.getTailOffset());
        final char lastChar = document.charAt(lastCharPos);
        final char firstChar = document.charAt(firstCharPos);
        final int firstNonQuoteCharPos = isQuoteChar(firstChar) ? firstCharPos + 1 : firstCharPos;
        final char firstNonQuoteChar = document.charAt(firstNonQuoteCharPos);

        if(lastChar != ':') {
            final int tailPos = getTailPos(context.getTailOffset(), lastChar);
            final String padText = isQuoteChar(firstChar) && !isQuoteChar(lastChar) ? firstChar+KEY_COLON : KEY_COLON;
            document.insertString(tailPos, padText);
            editor.getCaretModel().moveToOffset(tailPos + padText.length());
        } else {
            editor.getCaretModel().moveToOffset(document.lastCharPositionIgnoringSpaceAfter(lastCharPos + 1));
        }

        //special support for "@" char at the beginning
        if(firstNonQuoteChar == '@') {
            int endQuotePos = lastCharPos + 1;
            if(item.getLookupString().startsWith("@") && "@@".equals(document.substring(firstNonQuoteCharPos, firstNonQuoteCharPos + 2))) {
                document.deleteString(firstNonQuoteCharPos, firstNonQuoteCharPos + 1);
                endQuotePos--;
            }

            if(!isQuoteChar(firstChar)) {
                document.insertString(firstCharPos, "'");
                document.insertString(endQuotePos, "'");
            }
        }

        final DefaultValueDescriptor defaultValueDescriptor = item.getUserData(DefaultValueDescriptor.KEY);

        if(defaultValueDescriptor != null && item.getPsiElement() != null) {
            final Collection<String> properties = YamlPsiElements.getPropertyFrom(defaultValueDescriptor.valueFrom, item.getPsiElement());
            final String defaultValue = properties.stream()
                .map(defaultValueDescriptor.transformValue)
                .findAny()
                .orElse("");

            document.insertString(editor.getCaretModel().getOffset(), defaultValue);
            editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() + defaultValue.length());
        }

//        TODO: turn off because more sophisticated logic here is needed. AutoPopup should be shown only for choices and references
//        AutoPopupController.getInstance(editor.getProject()).scheduleAutoPopup(editor);
    }

    private int getTailPos(int pos, char lastChar) {
        return pos + (isQuoteChar(lastChar) ? 1 : 0);
    }

    private boolean isQuoteChar(char lastChar) {
        return lastChar == '\'' || lastChar == '"';
    }
}
