package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.openapi.editor.Document;

class DocumentOps {
    private final Document document;

    DocumentOps(Document document) {
        this.document = document;
    }

    int firstCharPositionBefore(int offset) {
        CharSequence charsSequence = document.getCharsSequence();

        for(int i=offset; i>1; i--) {
            char charAt = charsSequence.charAt(i-1);
            if(Character.isWhitespace(charAt)) {
                return i;
            }
        }

        return 1;
    }

    int lastCharPositionIgnoringSpaceAfter(int offset) {
        CharSequence charsSequence = document.getCharsSequence();

        for(int i=offset; i<charsSequence.length(); i++) {
            char charAt = charsSequence.charAt(i);
            if(charAt != ' ') {
                return i;
            }
        }

        return Math.min(offset, charsSequence.length() - 1);
    }

    char charAt(int offset) {
        return document.getCharsSequence().charAt(offset);
    }

    void insertString(int offset, String text) {
        document.insertString(offset, text);
    }

    void deleteString(int start, int end) {
        document.deleteString(start, end);
    }

    String substring(int start, int end) {
        return document.getCharsSequence().subSequence(start, end).toString();
    }
}
