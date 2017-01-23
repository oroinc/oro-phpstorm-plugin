package com.oroplatform.idea.oroplatform.schema;

import com.intellij.psi.PsiFile;

import java.util.Arrays;
import java.util.Collection;

class AndFileMatcher implements FileMatcher {

    private final Collection<FileMatcher> ops;

    AndFileMatcher(FileMatcher... ops) {
        this.ops = Arrays.asList(ops);
    }

    @Override
    public boolean matches(PsiFile file) {
        return ops.stream().allMatch(op -> op.matches(file));
    }
}
