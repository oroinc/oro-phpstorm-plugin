package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileContent;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.*;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

abstract class TranslationsDataIndexer implements DataIndexer<String, Void, FileContent> {
    @NotNull
    @Override
    public Map<String, Void> map(@NotNull FileContent inputData) {
        final Map<String, Void> index = new THashMap<String, Void>();

        if(!OroPlatformSettings.getInstance(inputData.getProject()).isPluginEnabled()) {
            return index;
        }

        final PhpFile file = (PhpFile) inputData.getPsiFile();

        for (PsiElement element : file.getChildren()) {
            if(!(element instanceof GroupStatement)) continue;

            final GroupStatement groupStatement = (GroupStatement) element;

            indexGroupStatement(index, groupStatement);
        }

        return index;
    }

    private void indexGroupStatement(Map<String, Void> index, GroupStatement groupStatement) {
        for (PsiElement potentialStatement : groupStatement.getStatements()) {
            if(!(potentialStatement instanceof Statement)) continue;

            final Statement statement = (Statement) potentialStatement;

            indexStatement(index, statement);
        }
    }

    private void indexStatement(Map<String, Void> index, Statement statement) {
        for (PsiElement potentialAssignment : statement.getChildren()) {
            if(!(potentialAssignment instanceof AssignmentExpression)) continue;

            final AssignmentExpression assignment = (AssignmentExpression) potentialAssignment;

            if(!(assignment.getValue() instanceof NewExpression)) continue;

            final NewExpression newExpression = (NewExpression) assignment.getValue();
            indexNewExpression(index, newExpression);

        }
    }

    private void indexNewExpression(Map<String, Void> index, NewExpression newExpression) {
        for (PsiElement parameter : newExpression.getParameters()) {
            if(!(parameter instanceof ArrayCreationExpression)) continue;

            final ArrayCreationExpression catalogue = (ArrayCreationExpression) parameter;

            indexCatalogue(index, catalogue);
        }
    }

    abstract void indexCatalogue(Map<String, Void> index, ArrayCreationExpression catalogue);
}
