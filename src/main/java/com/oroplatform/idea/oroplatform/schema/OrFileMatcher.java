package com.oroplatform.idea.oroplatform.schema;

import com.intellij.psi.PsiFile;

class OrFileMatcher implements FileMatcher {

    private final FileMatcher op1;
    private final FileMatcher op2;

    OrFileMatcher(FileMatcher op1, FileMatcher op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public boolean matches(PsiFile file) {
        return op1.matches(file) || op2.matches(file);
    }
}
